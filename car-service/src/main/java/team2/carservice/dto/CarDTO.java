package team2.carservice.dto;

import team2.carservice.entity.CarStatus;

import java.time.LocalDateTime;

public record CarDTO(
        Long id,
        String name,
        String engineId,
        String suspensionId,
        String aerodynamicsId,
        String gearboxId,
        String electronicsId,
        CarStatus status,
        LocalDateTime assembledAt
) {
}