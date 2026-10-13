package team2.driverservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import team2.driverservice.model.WeatherCondition;

@Schema(description = "Race conditions for driver recommendation and ranking")
public record RaceConditionsRequestDto(
        @NotNull(message = "Weather condition is required")
        @Schema(example = "WET")
        WeatherCondition weatherCondition,

        @Min(value = 0, message = "Risk level must be between 0 and 100")
        @Max(value = 100, message = "Risk level must be between 0 and 100")
        @Schema(example = "85")
        int riskLevel,

        @Min(value = 0, message = "Technical demand must be between 0 and 100")
        @Max(value = 100, message = "Technical demand must be between 0 and 100")
        @Schema(example = "90")
        int technicalDemand
) {
}

