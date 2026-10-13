package team2.carservice.mapper;

import org.springframework.stereotype.Component;
import team2.carservice.dto.CarDTO;
import team2.carservice.dto.CreateCarDTO;
import team2.carservice.entity.Car;
import team2.carservice.entity.CarStatus;

@Component
public class CarMapper {

    public Car toEntity(CreateCarDTO dto) {
        return new Car(
                null,
                dto.name(),
                null,
                null,
                null,
                null,
                null,
                CarStatus.NEW,
                null
        );
    }

    public CarDTO toDto(Car car) {
        return new CarDTO(
                car.getId(),
                car.getName(),
                car.getEngineId(),
                car.getSuspensionId(),
                car.getAerodynamicsId(),
                car.getGearboxId(),
                car.getElectronicsId(),
                car.getStatus(),
                car.getAssembledAt()
        );
    }
}