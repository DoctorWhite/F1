package team2.race_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import team2.race_service.api.Driver;
import team2.race_service.config.JmsProperties;
import team2.race_service.config.PointsConfig;
import team2.race_service.data.entity.DriverLap;
import team2.race_service.data.entity.Race;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.data.enums.Weather;
import team2.race_service.data.repository.RaceRepository;
import team2.race_service.exception.RaceNotFoundException;
import team2.race_service.messaging.RaceTelemetryEvent;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;
    private final PointsConfig pointsConfig;
    private final JmsTemplate jmsTemplate;
    private final JmsProperties jmsProperties;

    @Override
    public Race getRaceById(Long id) {
        return raceRepository.findById(id)
                .orElseThrow(() -> new RaceNotFoundException("Race not found with id: " + id));
    }

    @Override
    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    @Override
    public List<Race> getCompletedRaceByWeather(Weather weather) {
        return raceRepository.findCompletedRacesByWeather(weather);
    }

    @Override
    public Race createRace(Race race) {
        return raceRepository.save(race);
    }

    @Override
    public Race updateRace(Long id, Race newRace) {
        if (!raceRepository.existsById(id)) {
            throw new RaceNotFoundException("Cannot update: Race not found with id: " + id);
        }

        Race raceToUpdate = new Race(
                id,
                newRace.getLocation(),
                newRace.getDate(),
                newRace.getWeather(),
                newRace.getRaceResult()
        );

        return raceRepository.save(raceToUpdate);
    }

    @Override
    public void deleteRace(Long id) {
        if (!raceRepository.existsById(id)) {
            throw new RaceNotFoundException("Cannot delete: Race not found with id: " + id);
        }
        raceRepository.deleteById(id);
    }

    @Override
    public Race assignResultToRace(Long id, RaceResult result) {
        Race race = getRaceById(id);
        if (race.getDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Cannot assign results to a race that hasn't happened yet!");
        }

        Race updatedRace = new Race(race.getId(), race.getLocation(), race.getDate(), race.getWeather(), result, race.getDriverLaps());
        return raceRepository.save(updatedRace);
    }

    @Override
    @Transactional
    public Race addLapToRace(Long id, DriverLap lap) {
        Race race = raceRepository.findById(id)
                .orElseThrow(
                        () -> new RaceNotFoundException("Cannot add lap to race: Race with id: " + id + " does not exist")
                );

        race.addLap(lap);
        Race savedRace = raceRepository.save(race);
        publishRaceTelemetryEvent(lap);
        return savedRace;
    }

    @Override
    public Map<Driver, Integer> calculateRacePoints(Long raceId) {
        Race race = getRaceById(raceId);

        if (race.getRaceResult() == null) {
            throw new IllegalStateException("Race results have not been assigned yet.");
        }

        RaceResult result = race.getRaceResult();
        Map<Driver, Integer> racePoints = new HashMap<>();

        Long flDriverId = result.getFastestLapDriverId();

        Integer flPosition = getFastestLapDriverPosition(flDriverId, result);
        var flConfig = pointsConfig.getFastestLap();

        boolean isFlBonusEligible = flPosition != null && flPosition <= flConfig.getMinPosition();

        result.getLeaderboard().forEach((position, driver) -> {
            int points = pointsConfig.getPointsForPosition(position);

            if (isFlBonusEligible && driver.getId().equals(flDriverId)) {
                points += flConfig.getBonus();
            }

            racePoints.put(driver, points);
        });

        return racePoints;
    }

    private static @Nullable Integer getFastestLapDriverPosition(Long flDriverId, RaceResult result) {
        return (flDriverId == null)
                ? null
                : result.getLeaderboard().entrySet().stream()
                  .filter(entry -> entry.getValue().getId().equals(flDriverId))
                  .map(Map.Entry::getKey)
                  .findFirst()
                  .orElse(null);
    }

    private void publishRaceTelemetryEvent(DriverLap lap) {
        RaceTelemetryEvent event = new RaceTelemetryEvent(
                UUID.randomUUID().toString(),
                lap.getDriverId(),
                lap.getRace().getId(),
                List.of(new RaceTelemetryEvent.Reading(
                        lap.getFuelLevel(),
                        lap.getEngineTemp(),
                        lap.getTirePressure(),
                        lap.getLapTime()
                ))
        );
        jmsTemplate.convertAndSend(jmsProperties.telemetryQueue(), event);
        log.info("Published RaceTelemetryEvent for driver {} of race {} to queue {}",
                lap.getDriverId(), lap.getRace().getId(), jmsProperties.telemetryQueue());
    }
}