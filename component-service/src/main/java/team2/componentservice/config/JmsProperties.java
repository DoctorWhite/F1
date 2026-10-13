package team2.componentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "component-service.jms")
public record JmsProperties(
        String carAssemblyQueue,
        int listenerConcurrency
) {
}
