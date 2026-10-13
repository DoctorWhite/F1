package team2.race_service.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import team2.race_service.data.enums.Weather;

import java.time.LocalDate;

@Schema(description = "Request body for creating a new Formula 1 race")
public record CreateRaceDto(
        @Schema(description = "The city or circuit name", example = "Monaco")
        @NotBlank(message = "Race location is required")
        @Size(min = 3, max = 100)
        String location,

        @Schema(description = "The scheduled date of the race", example = "2026-05-24")
        @NotNull(message = "Race date is required")
        @FutureOrPresent(message = "New races must be scheduled for today or a future date")
        LocalDate date,

        @Schema(description = "Expected weather conditions", example = "SUNNY")
        @NotNull(message = "Race weather is required")
        Weather weather
) {
}