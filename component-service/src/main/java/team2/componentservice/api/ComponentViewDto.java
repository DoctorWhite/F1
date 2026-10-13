package team2.componentservice.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import team2.componentservice.data.enums.ComponentType;

public record ComponentViewDto(
    @Schema(description = "The component ID", example = "1")
    @NotNull(message = "Component ID is required")
    Long id,
    @Schema(description = "The component name", example = "V6 Turbo")
    @NotBlank(message = "Component name is required")
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
