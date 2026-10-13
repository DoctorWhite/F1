package team2.carservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "engine_id")
    private String engineId;

    @Column(name = "suspension_id")
    private String suspensionId;

    @Column(name = "aerodynamics_id")
    private String aerodynamicsId;

    @Column(name = "gearbox_id")
    private String gearboxId;

    @Column(name = "electronics_id")
    private String electronicsId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    @Column(name = "assembled_at")
    private LocalDateTime assembledAt;

    public boolean isAlreadyAssembled() {
        return status == CarStatus.ASSEMBLED;
    }
}