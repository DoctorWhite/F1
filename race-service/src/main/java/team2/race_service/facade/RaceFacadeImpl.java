package team2.race_service.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import team2.race_service.api.*;
import team2.race_service.data.entity.DriverLap;
import team2.race_service.data.entity.Race;
import team2.race_service.mapper.DriverLapMapper;
import team2.race_service.mapper.RaceMapper;
import team2.race_service.service.RaceService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceFacadeImpl implements RaceFacade {

    private final RaceService raceService;
    private final RaceMapper raceMapper;
    private final DriverLapMapper driverLapMapper;

    @Override
    public RaceViewDto findById(Long id) {
        Race race = raceService.getRaceById(id);
        return raceMapper.toViewDto(race);
    }

    @Override
    public Page<RaceViewDto> findAll(Pageable pageable) {
        List<Race> races = raceService.getAllRaces();

        List<RaceViewDto> dtos = races.stream()
                .map(raceMapper::toViewDto)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());

        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Override
    public RaceViewDto createRace(CreateRaceDto raceToCreate) {
        Race entity = raceMapper.toEntity(raceToCreate);
        Race savedRace = raceService.createRace(entity);
        return raceMapper.toViewDto(savedRace);
    }

    @Override
    public RaceViewDto assignResult(Long id, AssignRaceResultDto resultDto) {
        if (!id.equals(resultDto.raceId())) {
            throw new IllegalArgumentException("Path ID and Body ID do not match");
        }

        Race updatedRace = raceService.assignResultToRace(id, resultDto.raceResult());
        return raceMapper.toViewDto(updatedRace);
    }


    @Override
    public RaceViewDto updateRace(Long id, UpdateRaceDto raceToUpdate) {
        Race existing = raceService.getRaceById(id);
        existing.setLocation(raceToUpdate.location());
        existing.setDate(raceToUpdate.date());
        existing.setWeather(raceToUpdate.weather());
        Race updatedRace = raceService.updateRace(id, existing);
        return raceMapper.toViewDto(updatedRace);
    }

    @Override
    public RaceViewDto addLapToRace(Long id, DriverLapDto lapDto) {
        DriverLap lap = driverLapMapper.toEntity(lapDto);
        Race updatedRace = raceService.addLapToRace(id, lap);
        return raceMapper.toViewDto(updatedRace);
    }

    @Override
    public void deleteRace(Long id) {
        raceService.deleteRace(id);
    }

    @Override
    public RacePointsViewDto getRacePoints(Long raceId) {
        return new RacePointsViewDto(raceService.calculateRacePoints(raceId));
    }
}