package team2.driverservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.driverservice.model.DriverRaceStats;

import java.util.List;
import java.util.Optional;

public interface DriverRaceStatsRepository extends JpaRepository<DriverRaceStats, Long> {

    List<DriverRaceStats> findByDriverId(Long driverId);

    Optional<DriverRaceStats> findByDriverIdAndRaceId(Long driverId, Long raceId);
}
