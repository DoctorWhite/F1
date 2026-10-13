package team2.race_service.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import team2.race_service.data.entity.RaceResult;

@Schema(description = "Request body to assign or update the final standings of a race")
public record AssignRaceResultDto(
        @Schema(description = "The unique identifier of the race", example = "1")
        @NotNull(message = "Race ID is required")
        Long raceId,

        @Schema(description = "The official race results including the finishing order")
        @NotNull(message = "Race result data is required")
        @Valid
        RaceResult raceResult
) {
}