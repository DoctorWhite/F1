package team2.driverservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import team2.driverservice.model.DriverTelemetry;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DriverTelemetryRepositoryTest {

    @Autowired
    private DriverTelemetryRepository repository;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.save(telemetry(1L, 1L, false));
        repository.save(telemetry(1L, 1L, true));
        repository.save(telemetry(2L, 1L, false));
    }

    @Test
    void findByProcessedFalse_returnsOnlyUnprocessedRecords() {
        List<DriverTelemetry> result = repository.findByProcessedFalse();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(t -> !t.isProcessed());
    }

    @Test
    void findByProcessedFalse_afterMarkingAllProcessed_returnsEmpty() {
        repository.findAll().forEach(t -> t.setProcessed(true));
        repository.saveAll(repository.findAll());

        assertThat(repository.findByProcessedFalse()).isEmpty();
    }

    @Test
    void findByDriverIdAndRaceId_returnsOnlyMatchingRecords() {
        List<DriverTelemetry> result = repository.findByDriverIdAndRaceId(1L, 1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(t -> t.getDriverId().equals(1L));
        assertThat(result).allMatch(t -> t.getRaceId().equals(1L));
    }

    @Test
    void findByDriverIdAndRaceId_differentDriver_returnsEmpty() {
        List<DriverTelemetry> result = repository.findByDriverIdAndRaceId(99L, 1L);

        assertThat(result).isEmpty();
    }

    private DriverTelemetry telemetry(Long driverId, Long raceId, boolean processed) {
        return DriverTelemetry.builder()
                .driverId(driverId)
                .raceId(raceId)
                .recordedAt(now)
                .fuelLevel(80.0)
                .engineTemp(105.0)
                .tirePressure(24.5)
                .lapTime(92.0)
                .processed(processed)
                .build();
    }
}
