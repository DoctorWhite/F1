package team2.race_service.mapper;

import org.springframework.stereotype.Component;
import team2.race_service.api.RaceResultViewDto;
import team2.race_service.data.entity.RaceResult;

@Component
public class RaceResultMapper {

    public RaceResultViewDto toViewDto(RaceResult entity) {
        return new RaceResultViewDto(
                entity.getId(),
                entity.getLeaderboard(),
                entity.getFastestLapDriverId()
        );
    }
}
