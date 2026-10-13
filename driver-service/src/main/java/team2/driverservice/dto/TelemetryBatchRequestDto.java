package team2.driverservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TelemetryBatchRequestDto(
        @NotNull Long driverId,
        @NotNull Long raceId,
        @NotEmpty @Valid List<TelemetryRecordDto> readings
) {
}
