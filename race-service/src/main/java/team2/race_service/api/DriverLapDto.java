package team2.race_service.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for adding new lap to the race")
public record DriverLapDto(

        @Schema(description = "The unique identifier of the driver", example = "1")
        @NotNull(message = "Driver ID is required")
        Long driverId,

        @Schema(description = "Remaining fuel level as a percentage (0–100)", example = "75.5")
        @NotNull(message = "Fuel level is required")
        @DecimalMin(value = "0.0", message = "Fuel level cannot be negative")
        @DecimalMax(value = "100.0", message = "Fuel level cannot exceed 100%")
        Double fuelLevel,

        @Schema(description = "Engine temperature in degrees Celsius", example = "95.3")
        @NotNull(message = "Engine temperature is required")
        @DecimalMin(value = "0.0", message = "Engine temperature cannot be negative")
        @DecimalMax(value = "200.0", message = "Engine temperature is unrealistically high")
        Double engineTemp,

        @Schema(description = "Tire pressure in PSI", example = "23.5")
        @NotNull(message = "Tire pressure is required")
        @Positive(message = "Tire pressure must be positive")
        Double tirePressure,

        @Schema(description = "Lap time in seconds", example = "92.347")
        @NotNull(message = "Lap time is required")
        @Positive(message = "Lap time must be positive")
        Double lapTime
) {
}