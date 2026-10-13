package team2.carservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "car-service.jms")
public record JmsProperties(
        String carAssemblyQueue
) {
}
