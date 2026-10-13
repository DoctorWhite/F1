package team2.driverservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Suggested race lineup")
public record DriverLineupSuggestionDto(
        @NotNull(message = "Primary driver cannot be null")
        DriverResponseDto primaryDriver,

        @NotNull(message = "Secondary driver cannot be null")
        DriverResponseDto secondaryDriver,

        @NotBlank(message = "Rationale is required")
        String rationale
) {
}

