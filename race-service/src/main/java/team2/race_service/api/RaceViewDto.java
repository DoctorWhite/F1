package team2.race_service.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import team2.race_service.data.entity.DriverLap;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.data.enums.Weather;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Detailed view of a Formula 1 race, including results if available")
public record RaceViewDto(
        @Schema(description = "The ID of the race", example = "1")
        @NotNull
        Long id,

        @Schema(description = "The city or circuit name", example = "Monza")
        @NotBlank
        String location,

        @Schema(description = "The scheduled date of the race", example = "2026-09-06")
        @NotNull
        LocalDate date,

        @Schema(description = "Weather conditions at the time of the race", example = "CLOUDY")
        @NotNull
        Weather weather,

        @Schema(
                description = "The final results of the race. This field is null for upcoming races.",
                nullable = true
        )
        @Nullable
        @Valid
        RaceResult raceResult,

        @Schema(
                description = "Finished laps of particular race."
        )
        @Valid
        List<DriverLap> laps
) {
}