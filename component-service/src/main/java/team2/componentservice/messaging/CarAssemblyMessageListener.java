package team2.componentservice.messaging;

import tools.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team2.componentservice.data.entity.CarAssemblyInboundMessage;
import team2.componentservice.data.repository.CarAssemblyInboundMessageRepository;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CarAssemblyMessageListener {

    private final CarAssemblyInboundMessageRepository repository;
    private final JsonMapper jsonMapper;

    @JmsListener(
            destination = "${component-service.jms.car-assembly-queue}",
            containerFactory = "jmsListenerContainerFactory"
    )
    @Transactional
    public void onCarAssemblyCompleted(CarAssemblyCompletedEvent event) {
        if (event.messageId() == null || event.messageId().isBlank()) {
            throw new IllegalArgumentException("messageId is required");
        }
        if (repository.existsByMessageId(event.messageId())) {
            return;
        }
        String json;
        try {
            json = jsonMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize car assembly event", e);
        }
        CarAssemblyInboundMessage row = new CarAssemblyInboundMessage(
                null,
                event.messageId(),
                event.carId(),
                event.assembledAt(),
                json,
                Instant.now()
        );
        repository.save(row);
    }
}
