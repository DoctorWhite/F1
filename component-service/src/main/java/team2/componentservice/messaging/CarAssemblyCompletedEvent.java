package team2.componentservice.messaging;

import java.time.Instant;

/**
 * JSON payload for car assembly notifications. The producer must set the JMS JSON type id
 * ({@code _type}) to {@code CarAssemblyCompletedEvent}, matching how driver-service maps
 * {@code RaceTelemetryEvent} in its {@code JacksonJsonMessageConverter}.
 */
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
