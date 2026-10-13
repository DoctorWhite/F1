package team2.race_service.api;

import java.util.Map;

public record RacePointsViewDto(
        Map<Driver, Integer> racePoints
) {
}
