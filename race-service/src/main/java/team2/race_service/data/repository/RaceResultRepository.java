package team2.race_service.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.race_service.data.entity.RaceResult;

import java.util.List;

public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {

    // Finds all race results with specific driver id as fastest lap driver
    List<RaceResult> findByFastestLapDriverId(Long driverId);
}
