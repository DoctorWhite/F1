package team2.driverservice.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;
import team2.driverservice.model.DriverRaceStats;
import team2.driverservice.model.DriverTelemetry;
import team2.driverservice.repository.DriverRaceStatsRepository;
import team2.driverservice.repository.DriverTelemetryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TelemetryStatsWriter implements ItemWriter<DriverTelemetry> {

    private final DriverRaceStatsRepository raceStatsRepository;
    private final DriverTelemetryRepository telemetryRepository;

    @Override
    public void write(Chunk<? extends DriverTelemetry> chunk) {
        Map<String, List<DriverTelemetry>> grouped = chunk.getItems().stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDriverId() + ":" + t.getRaceId(),
                        Collectors.mapping(t -> (DriverTelemetry) t, Collectors.toList())
                ));

        for (List<DriverTelemetry> chunkRecords : grouped.values()) {
            DriverTelemetry sample = chunkRecords.getFirst();
            Long driverId = sample.getDriverId();
            Long raceId = sample.getRaceId();

            List<DriverTelemetry> allRecords =
                    telemetryRepository.findByDriverIdAndRaceId(driverId, raceId);

            DriverRaceStats stats = raceStatsRepository
                    .findByDriverIdAndRaceId(driverId, raceId)
                    .orElseGet(() -> DriverRaceStats.builder()
                            .driverId(driverId)
                            .raceId(raceId)
                            .build());

            double[] lapTimes = allRecords.stream().mapToDouble(DriverTelemetry::getLapTime).toArray();

            stats.setAvgFuelLevel(average(allRecords.stream().mapToDouble(DriverTelemetry::getFuelLevel).toArray()));
            stats.setAvgEngineTemp(average(allRecords.stream().mapToDouble(DriverTelemetry::getEngineTemp).toArray()));
            stats.setAvgTirePressure(average(allRecords.stream().mapToDouble(DriverTelemetry::getTirePressure).toArray()));
            stats.setAvgLapTime(average(lapTimes));
            stats.setDerivedConsistency(computeConsistency(lapTimes));
            stats.setSampleCount(allRecords.size());
            stats.setProcessedAt(LocalDateTime.now());

            raceStatsRepository.save(stats);
        }

        telemetryRepository.saveAll(chunk.getItems());
    }

    private double average(double[] values) {
        if (values.length == 0) {
            return 0.0;
        }
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return Math.round((sum / values.length) * 100.0) / 100.0;
    }

    private double computeConsistency(double[] lapTimes) {
        if (lapTimes.length < 2) {
            return 100.0;
        }
        double avg = average(lapTimes);
        double variance = 0;
        for (double t : lapTimes) {
            variance += Math.pow(t - avg, 2);
        }
        double stdDev = Math.sqrt(variance / lapTimes.length);
        return Math.max(0, Math.round((100.0 - stdDev * 10) * 100.0) / 100.0);
    }
}
