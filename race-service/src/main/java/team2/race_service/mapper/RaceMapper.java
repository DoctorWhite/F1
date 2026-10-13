package team2.race_service.mapper;

import org.springframework.stereotype.Component;
import team2.race_service.api.CreateRaceDto;
import team2.race_service.api.RaceViewDto;
import team2.race_service.data.entity.Race;

@Component
public class RaceMapper {

    public Race toEntity(CreateRaceDto dto) {
        return new Race(null, dto.location(), dto.date(), dto.weather(), null);
    }

    public RaceViewDto toViewDto(Race entity) {
        return new RaceViewDto(
                entity.getId(),
                entity.getLocation(),
                entity.getDate(),
                entity.getWeather(),
                entity.getRaceResult(),
                entity.getDriverLaps()
        );
    }
}