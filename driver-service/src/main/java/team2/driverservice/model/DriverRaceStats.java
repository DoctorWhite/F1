package team2.driverservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_race_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRaceStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(name = "race_id", nullable = false)
    private Long raceId;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "avg_fuel_level")
    private double avgFuelLevel;

    @Column(name = "avg_engine_temp")
    private double avgEngineTemp;

    @Column(name = "avg_tire_pressure")
    private double avgTirePressure;

    @Column(name = "avg_lap_time")
    private double avgLapTime;

    /**
     * 0–100; derived from lap-time standard deviation — higher means more consistent.
     */
    @Column(name = "derived_consistency")
    private double derivedConsistency;

    @Column(name = "sample_count")
    private int sampleCount;
}
