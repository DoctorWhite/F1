package team2.race_service.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(description = "Detailed view of a Formula 1 race result")
public record RaceResultViewDto(
        @Schema(description = "The ID of the race result", example = "1")
        @NotNull
        Long id,

        @Schema(description = "Leaderboard saved within the race result", example = "")
        @NotBlank
        Map<Integer, @Valid Driver> leaderboard,

        @Schema(description = "The ID of the fastest lap driver", example = "1")
        @NotNull
        Long fastestLapDriverId
) {
}
