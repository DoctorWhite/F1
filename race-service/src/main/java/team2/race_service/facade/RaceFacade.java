package team2.race_service.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import team2.race_service.api.*;

public interface RaceFacade {
    RaceViewDto findById(Long id);

    Page<RaceViewDto> findAll(Pageable pageable);

    RaceViewDto createRace(CreateRaceDto raceToCreate);

    RaceViewDto assignResult(Long id, AssignRaceResultDto raceResultDto);

    RaceViewDto updateRace(Long id, UpdateRaceDto raceToUpdate);

    RaceViewDto addLapToRace(Long id, DriverLapDto lapDto);

    void deleteRace(Long id);

    RacePointsViewDto getRacePoints(Long raceId);
}
