package team2.race_service.data.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import team2.race_service.data.enums.Weather;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "races")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Schema(description = "The primary domain entity representing a Formula 1 Grand Prix")
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique database identifier", example = "1")
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Location cannot be empty")
    @Size(min = 3, max = 100)
    @Schema(description = "Circuit or City name", example = "Spa-Francorchamps")
    private String location;

    @Column(nullable = false)
    @NotNull(message = "Race date is required")
    @Schema(description = "The date the race is held", example = "2026-08-30")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Weather condition must be specified")
    @Schema(description = "Predominant weather condition")
    private Weather weather;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "race_result_id")
    @Valid
    @Schema(description = "Official race results (null if race has not occurred)", nullable = true)
    private RaceResult raceResult;

    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    @Schema(description = "Laps finished within this race by a specific driver")
    @JsonManagedReference
    private List<DriverLap> driverLaps = new ArrayList<>();

    public Race(Long id, String location, LocalDate date, Weather weather, RaceResult raceResult) {
        this(id, location, date, weather, raceResult, new ArrayList<>());
    }

    @Schema(hidden = true)
    public boolean isCompleted() {
        return raceResult != null && !date.isAfter(LocalDate.now());
    }

    @Schema(hidden = true)
    public void addLap(DriverLap lap) {
        driverLaps.add(lap);
        lap.setRace(this);
    }
}