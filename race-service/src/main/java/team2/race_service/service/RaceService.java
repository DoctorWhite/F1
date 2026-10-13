package team2.race_service.service;

import team2.race_service.api.Driver;
import team2.race_service.data.entity.DriverLap;
import team2.race_service.data.entity.Race;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.data.enums.Weather;

import java.util.List;
import java.util.Map;

public interface RaceService {
    Race getRaceById(Long id);

    List<Race> getAllRaces();

    List<Race> getCompletedRaceByWeather(Weather weather);

    Race createRace(Race race);

    Race updateRace(Long id, Race newRace);

    Race addLapToRace(Long id, DriverLap lap);

    void deleteRace(Long id);

    Race assignResultToRace(Long id, RaceResult result);

    Map<Driver, Integer> calculateRacePoints(Long raceId);
}
