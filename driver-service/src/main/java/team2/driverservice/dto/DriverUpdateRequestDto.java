package team2.driverservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import team2.driverservice.model.DriverRole;

@Schema(description = "Request for updating an existing driver")
public record DriverUpdateRequestDto(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Nationality is required")
        String nationality,

        @NotNull(message = "Driver role is required")
        DriverRole role,

        @Min(value = 0, message = "Wet skill must be between 0 and 100")
        @Max(value = 100, message = "Wet skill must be between 0 and 100")
        int wetSkill,

        @Min(value = 0, message = "Aggressiveness must be between 0 and 100")
        @Max(value = 100, message = "Aggressiveness must be between 0 and 100")
        int aggressiveness,

        @Min(value = 0, message = "Consistency must be between 0 and 100")
        @Max(value = 100, message = "Consistency must be between 0 and 100")
        int consistency,

        @Min(value = 0, message = "Feedback quality must be between 0 and 100")
        @Max(value = 100, message = "Feedback quality must be between 0 and 100")
        int feedbackQuality
) {
}

