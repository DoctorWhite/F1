package team2.driverservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.driverservice.model.ReceivedTelemetryMessage;

public interface ReceivedTelemetryMessageRepository extends JpaRepository<ReceivedTelemetryMessage, Long> {

    boolean existsByMessageId(String messageId);
}
