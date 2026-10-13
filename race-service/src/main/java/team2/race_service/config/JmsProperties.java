package team2.race_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "race-service.jms")
public record JmsProperties(
        String telemetryQueue
) {
}