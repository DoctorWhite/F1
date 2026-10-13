package team2.race_service.data.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import team2.race_service.api.Driver;
import team2.race_service.api.validation.ValidLeaderboard;
import team2.race_service.config.LeaderboardConverter;

import java.util.Map;

@Entity
@Table(name = "race_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Schema(description = "The final leaderboard mapping finishing positions to drivers")
@ValidLeaderboard
public class RaceResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique database identifier", example = "1")
    private Long id;

    @Convert(converter = LeaderboardConverter.class)
    @Column(name = "leaderboard", nullable = false, length = 5000)
    @Schema(description = "Map of finishing position (Key) to Driver details (Value)",
            example = "{ \"1\": { \"id\": 1, \"firstName\": \"Max\", \"lastName\": \"Verstappen\" } }")
    @NotEmpty(message = "The leaderboard must contain at least one result")
    private Map<Integer, @Valid Driver> leaderboard;

    @Column(name = "fastest_lap_driver_id")
    private Long fastestLapDriverId;
}