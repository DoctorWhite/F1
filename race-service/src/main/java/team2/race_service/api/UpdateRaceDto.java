package team2.race_service.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import team2.race_service.data.enums.Weather;

import java.time.LocalDate;

@Schema(description = "Request body for updating an existing race's details")
public record UpdateRaceDto(
        @Schema(description = "Updated location name", example = "Silverstone")
        @NotBlank(message = "Race location is required")
        @Size(min = 3, max = 100)
        String location,

        @Schema(description = "Updated race date (can be historical for corrections)", example = "2026-07-05")
        @NotNull(message = "Race date is required")
        LocalDate date,

        @Schema(description = "Updated weather conditions", example = "RAINY")
        @NotNull(message = "Race weather is required")
        Weather weather
) {
}