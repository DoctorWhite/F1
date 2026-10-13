package team2.race_service.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

@Entity
@Table(name = "driver_laps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Schema(description = "Entity representing driver's lap in a certain race")
public class DriverLap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_id")
    private Long driverId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "race_id")
    @Valid
    @Schema(description = "Id of the race the lap was finished at", nullable = true)
    @JsonBackReference
    private Race race;

    @Column(name = "fuel_level")
    @Valid
    private double fuelLevel;

    @Column(name = "engine_temp")
    private double engineTemp;

    @Column(name = "tire_pressure")
    private double tirePressure;

    @Column(name = "lap_time")
    private double lapTime;
}
