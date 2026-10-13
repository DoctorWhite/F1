package team2.driverservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import team2.driverservice.model.WeatherCondition;

@Schema(description = "Recommendation result for race conditions")
public record DriverRecommendationDto(
        @NotNull(message = "Recommended driver cannot be null")
        DriverResponseDto recommendedDriver,

        @Schema(example = "95.5")
        double suitabilityScore,

        @NotNull(message = "Weather condition cannot be null")
        WeatherCondition weatherCondition,

        @NotBlank(message = "Explanation is required")
        String explanation
) {
}

