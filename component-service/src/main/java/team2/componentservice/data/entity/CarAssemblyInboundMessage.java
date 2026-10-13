package team2.componentservice.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "car_assembly_inbound_messages",
        uniqueConstraints = @UniqueConstraint(name = "uk_car_assembly_message_id", columnNames = "message_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarAssemblyInboundMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, length = 64)
    private String messageId;

    @Column(name = "car_id", nullable = false)
    private Long carId;

    @Column(name = "assembled_at", nullable = false)
    private Instant assembledAt;

    @Lob
    @Column(name = "payload_json", nullable = false)
    private String payloadJson;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;
}
