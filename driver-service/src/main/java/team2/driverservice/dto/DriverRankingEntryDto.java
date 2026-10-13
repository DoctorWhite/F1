package team2.driverservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Single ranked driver result")
public record DriverRankingEntryDto(
        @NotNull(message = "Driver cannot be null")
        DriverResponseDto driver,

        @Schema(example = "92.5")
        double score,

        @Min(value = 1, message = "Position must be at least 1")
        int position
) {
}

