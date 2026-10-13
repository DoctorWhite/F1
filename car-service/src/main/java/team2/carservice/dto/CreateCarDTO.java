package team2.carservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCarDTO(
        @NotBlank(message = "Car name must not be blank")
        String name
) {
}