package team2.driverservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.driverservice.model.DriverTelemetry;

import java.util.List;

public interface DriverTelemetryRepository extends JpaRepository<DriverTelemetry, Long> {

    List<DriverTelemetry> findByProcessedFalse();

    List<DriverTelemetry> findByDriverIdAndRaceId(Long driverId, Long raceId);
}
