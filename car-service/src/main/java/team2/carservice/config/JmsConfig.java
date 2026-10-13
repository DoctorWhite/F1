package team2.carservice.config;

import org.springframework.boot.artemis.autoconfigure.ArtemisConfigurationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageType;
import team2.carservice.messaging.CarAssemblyCompletedEvent;

import java.util.Map;

@Configuration
@EnableJms
@EnableConfigurationProperties(JmsProperties.class)
public class JmsConfig {

    @Bean
    public ArtemisConfigurationCustomizer artemisConfigurationCustomizer() {
        return configuration -> configuration.setJMXManagementEnabled(false);
    }

    @Bean
    public JacksonJsonMessageConverter jmsMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setTypeIdMappings(Map.of("CarAssemblyCompletedEvent", CarAssemblyCompletedEvent.class));
        return converter;
    }
}