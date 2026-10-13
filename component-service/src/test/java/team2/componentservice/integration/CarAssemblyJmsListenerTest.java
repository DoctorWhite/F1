package team2.componentservice.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import team2.componentservice.config.JmsProperties;
import team2.componentservice.data.entity.CarAssemblyInboundMessage;
import team2.componentservice.data.repository.CarAssemblyInboundMessageRepository;
import team2.componentservice.messaging.CarAssemblyCompletedEvent;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CarAssemblyJmsListenerTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsProperties jmsProperties;

    @Autowired
    private CarAssemblyInboundMessageRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void carAssemblyCompletedMessage_isPersisted() throws InterruptedException {
        String messageId = UUID.randomUUID().toString();
        CarAssemblyCompletedEvent event = new CarAssemblyCompletedEvent(
                messageId,
                99L,
                Instant.parse("2026-04-21T12:00:00Z"),
                "engine-1",
                "suspension-1",
                "aero-1",
                "gearbox-1",
                "electronics-1"
        );

        jmsTemplate.convertAndSend(jmsProperties.carAssemblyQueue(), event);

        waitForRows(1);

        CarAssemblyInboundMessage row = repository.findAll().getFirst();
        assertThat(row.getMessageId()).isEqualTo(messageId);
        assertThat(row.getCarId()).isEqualTo(99L);
        assertThat(row.getAssembledAt()).isEqualTo(event.assembledAt());
        assertThat(row.getPayloadJson()).contains(messageId).contains("\"carId\":99");
        assertThat(row.getReceivedAt()).isNotNull();
    }

    @Test
    void duplicateMessageId_secondDelivery_isIgnored() throws InterruptedException {
        String messageId = UUID.randomUUID().toString();
        CarAssemblyCompletedEvent event = new CarAssemblyCompletedEvent(
                messageId,
                1L,
                Instant.now(),
                "e", "s", "a", "g", "el"
        );

        jmsTemplate.convertAndSend(jmsProperties.carAssemblyQueue(), event);
        waitForRows(1);
        jmsTemplate.convertAndSend(jmsProperties.carAssemblyQueue(), event);
        Thread.sleep(300);

        assertThat(repository.count()).isEqualTo(1);
    }

    private void waitForRows(int expected) throws InterruptedException {
        int attempts = 0;
        while (repository.count() < expected && attempts < 100) {
            Thread.sleep(50);
            attempts++;
        }
        assertThat(repository.count()).isEqualTo(expected);
    }
}
