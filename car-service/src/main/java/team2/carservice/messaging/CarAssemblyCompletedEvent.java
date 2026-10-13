package team2.carservice.messaging;

import java.time.Instant;

public record CarAssemblyCompletedEvent(
        String messageId,
        Long carId,
        Instant assembledAt,
        String engineId,
        String suspensionId,
        String aerodynamicsId,
        String gearboxId,
        String electronicsId
) {
}
