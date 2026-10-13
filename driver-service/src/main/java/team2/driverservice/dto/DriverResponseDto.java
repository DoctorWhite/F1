package team2.driverservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import team2.driverservice.model.DriverRole;

@Schema(description = "Driver details")
public record DriverResponseDto(
        @NotNull
        Long id,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String nationality,

        @NotNull
        DriverRole role,

        @Min(0) @Max(100)
        int wetSkill,

        @Min(0) @Max(100)
        int aggressiveness,

        @Min(0) @Max(100)
        int consistency,

        @Min(0) @Max(100)
        int feedbackQuality
) {
}
