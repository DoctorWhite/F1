package team2.carservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssembleCarRequestDTO(
        @NotNull(message = "Car id must not be null")
        Long carId,

        @NotBlank(message = "Engine id must not be blank")
        String engineId,

        @NotBlank(message = "Suspension id must not be blank")
        String suspensionId,

        @NotBlank(message = "Aerodynamics id must not be blank")
        String aerodynamicsId,

        @NotBlank(message = "Gearbox id must not be blank")
        String gearboxId,

        @NotBlank(message = "Electronics id must not be blank")
        String electronicsId
) {
}