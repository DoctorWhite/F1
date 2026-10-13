package team2.race_service.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team2.race_service.api.RaceResultViewDto;

public interface RaceResultFacade {

    RaceResultViewDto findById(Long id);

    Page<RaceResultViewDto> findAll(Pageable pageable);

    Page<RaceResultViewDto> findRaceResultsForFastestLapDriverId(Pageable pageable, Long driverId);
}
