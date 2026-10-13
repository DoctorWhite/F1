package team2.componentservice.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.boot.artemis.autoconfigure.ArtemisConfigurationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageType;
import team2.componentservice.messaging.CarAssemblyCompletedEvent;

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

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter jmsMessageConverter,
            JmsProperties jmsProperties
    ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jmsMessageConverter);
        factory.setSessionTransacted(true);
        factory.setConcurrency(String.valueOf(jmsProperties.listenerConcurrency()));
        return factory;
    }
}
