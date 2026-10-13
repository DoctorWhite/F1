package team2.driverservice.dto;

import java.time.LocalDateTime;

public record DriverRaceStatsDto(
        Long id,
        Long driverId,
        Long raceId,
        LocalDateTime processedAt,
        double avgFuelLevel,
        double avgEngineTemp,
        double avgTirePressure,
        double avgLapTime,
        double derivedConsistency,
        int sampleCount
) {
}
