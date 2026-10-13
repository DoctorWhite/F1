package team2.driverservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_telemetry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverTelemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(name = "race_id", nullable = false)
    private Long raceId;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "fuel_level")
    private double fuelLevel;

    @Column(name = "engine_temp")
    private double engineTemp;

    @Column(name = "tire_pressure")
    private double tirePressure;

    @Column(name = "lap_time")
    private double lapTime;

    @Column(nullable = false)
    @Builder.Default
    private boolean processed = false;
}
