package team2.race_service.service;

import team2.race_service.data.entity.RaceResult;

import java.util.List;

public interface RaceResultService {
    RaceResult getRaceResultById(Long id);

    List<RaceResult> getAllRaceResults();

    List<RaceResult> getRaceResultsForFastestLapDriverId(Long driverId);
}
