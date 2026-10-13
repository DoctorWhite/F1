package team2.driverservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import team2.driverservice.model.DriverRaceStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DriverRaceStatsRepositoryTest {

    @Autowired
    private DriverRaceStatsRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.save(stats(1L, 1L, 91.5, 98.0));
        repository.save(stats(1L, 2L, 89.2, 95.0));
        repository.save(stats(2L, 1L, 92.1, 90.0));
    }

    @Test
    void findByDriverId_returnsAllStatsForDriver() {
        List<DriverRaceStats> result = repository.findByDriverId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DriverRaceStats::getRaceId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void findByDriverId_unknownDriver_returnsEmpty() {
        assertThat(repository.findByDriverId(99L)).isEmpty();
    }

    @Test
    void findByDriverIdAndRaceId_exactMatch_returnsRecord() {
        Optional<DriverRaceStats> result = repository.findByDriverIdAndRaceId(1L, 1L);

        assertThat(result).isPresent();
        assertThat(result.get().getAvgLapTime()).isEqualTo(91.5);
        assertThat(result.get().getDerivedConsistency()).isEqualTo(98.0);
    }

    @Test
    void findByDriverIdAndRaceId_noMatch_returnsEmpty() {
        assertThat(repository.findByDriverIdAndRaceId(1L, 3L)).isEmpty();
    }

    private DriverRaceStats stats(Long driverId, Long raceId, double avgLapTime, double consistency) {
        return DriverRaceStats.builder()
                .driverId(driverId)
                .raceId(raceId)
                .processedAt(LocalDateTime.now())
                .avgFuelLevel(75.0)
                .avgEngineTemp(106.0)
                .avgTirePressure(24.3)
                .avgLapTime(avgLapTime)
                .derivedConsistency(consistency)
                .sampleCount(10)
                .build();
    }
}
