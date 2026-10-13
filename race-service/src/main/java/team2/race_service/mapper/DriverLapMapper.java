package team2.race_service.mapper;

import org.springframework.stereotype.Component;
import team2.race_service.api.DriverLapDto;
import team2.race_service.data.entity.DriverLap;

@Component
public class DriverLapMapper {
    public DriverLap toEntity(DriverLapDto dto) {
        DriverLap lap = new DriverLap();
        lap.setDriverId(dto.driverId());
        lap.setFuelLevel(dto.fuelLevel());
        lap.setEngineTemp(dto.engineTemp());
        lap.setTirePressure(dto.tirePressure());
        lap.setLapTime(dto.lapTime());
        return lap;
    }
}
