package team2.driverservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

public record TelemetryRecordDto(
        @DecimalMin("0.0") @DecimalMax("100.0") double fuelLevel,
        @Positive double engineTemp,
        @Positive double tirePressure,
        @Positive double lapTime
) {
}
