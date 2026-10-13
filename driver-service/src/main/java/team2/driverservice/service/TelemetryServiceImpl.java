package team2.driverservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.driverservice.dto.DriverRaceStatsDto;
import team2.driverservice.dto.TelemetryBatchRequestDto;
import team2.driverservice.dto.TelemetryRecordDto;
import team2.driverservice.exception.DriverNotFoundException;
import team2.driverservice.model.DriverTelemetry;
import team2.driverservice.repository.DriverRaceStatsRepository;
import team2.driverservice.repository.DriverRepository;
import team2.driverservice.repository.DriverTelemetryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TelemetryServiceImpl implements TelemetryService {

    private final DriverTelemetryRepository telemetryRepository;
    private final DriverRaceStatsRepository raceStatsRepository;
    private final DriverRepository driverRepository;

    @Override
    @Transactional
    public void ingestBatch(TelemetryBatchRequestDto request) {
        if (!driverRepository.existsById(request.driverId())) {
            throw new DriverNotFoundException(request.driverId());
        }

        List<DriverTelemetry> records = request.readings().stream()
                .map(r -> toEntity(request.driverId(), request.raceId(), r))
                .toList();

        telemetryRepository.saveAll(records);
    }

    @Override
    public List<DriverRaceStatsDto> getRaceStats(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException(driverId);
        }
        return raceStatsRepository.findByDriverId(driverId).stream()
                .map(s -> new DriverRaceStatsDto(
                        s.getId(),
                        s.getDriverId(),
                        s.getRaceId(),
                        s.getProcessedAt(),
                        s.getAvgFuelLevel(),
                        s.getAvgEngineTemp(),
                        s.getAvgTirePressure(),
                        s.getAvgLapTime(),
                        s.getDerivedConsistency(),
                        s.getSampleCount()))
                .toList();
    }

    private DriverTelemetry toEntity(Long driverId, Long raceId, TelemetryRecordDto r) {
        return DriverTelemetry.builder()
                .driverId(driverId)
                .raceId(raceId)
                .recordedAt(LocalDateTime.now())
                .fuelLevel(r.fuelLevel())
                .engineTemp(r.engineTemp())
                .tirePressure(r.tirePressure())
                .lapTime(r.lapTime())
                .build();
    }
}
