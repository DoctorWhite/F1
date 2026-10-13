package team2.componentservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.componentservice.data.entity.CarAssemblyInboundMessage;

public interface CarAssemblyInboundMessageRepository extends JpaRepository<CarAssemblyInboundMessage, Long> {

    boolean existsByMessageId(String messageId);
}
