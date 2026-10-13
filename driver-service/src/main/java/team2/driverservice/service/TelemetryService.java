package team2.driverservice.service;

import team2.driverservice.dto.DriverRaceStatsDto;
import team2.driverservice.dto.TelemetryBatchRequestDto;

import java.util.List;

public interface TelemetryService {

    void ingestBatch(TelemetryBatchRequestDto request);

    List<DriverRaceStatsDto> getRaceStats(Long driverId);
}
