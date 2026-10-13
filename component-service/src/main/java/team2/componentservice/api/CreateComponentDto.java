package team2.componentservice.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import team2.componentservice.data.enums.ComponentType;

@Schema(description = "Request body for creating a new Formula 1 component (ID is assigned by the server)")
public record CreateComponentDto(
        @Schema(description = "The component name", example = "V6 Turbo")
        @NotBlank(message = "Component name is required")
        @Size(min = 3, max = 100)
        String name,

        @Schema(description = "The component type", example = "ENGINE")
        @NotNull(message = "Component type is required")
        ComponentType type,

        @Schema(description = "The component version", example = "2.1")
        @NotBlank(message = "Component version is required")
        String version,

        @Schema(description = "The component performance score", example = "95")
        @NotNull(message = "Component performance score is required")
        Double performanceScore,

        @Schema(description = "The component reliability score", example = "90")
        @NotNull(message = "Component reliability score is required")
        Double reliabilityScore,

        @Schema(description = "The component department", example = "Powertrain")
        @NotBlank(message = "Component department is required")
        String department
) {
}
