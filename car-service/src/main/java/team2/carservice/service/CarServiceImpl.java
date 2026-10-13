package team2.carservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.carservice.config.JmsProperties;
import team2.carservice.dto.AssembleCarRequestDTO;
import team2.carservice.entity.Car;
import team2.carservice.entity.CarStatus;
import team2.carservice.exception.CarAssemblyException;
import team2.carservice.exception.CarNotFoundException;
import team2.carservice.messaging.CarAssemblyCompletedEvent;
import team2.carservice.repository.CarRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final JmsTemplate jmsTemplate;
    private final JmsProperties jmsProperties;

    public CarServiceImpl(
            CarRepository carRepository,
            JmsTemplate jmsTemplate,
            JmsProperties jmsProperties
    ) {
        this.carRepository = carRepository;
        this.jmsTemplate = jmsTemplate;
        this.jmsProperties = jmsProperties;
    }

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public Car findById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
    }

    @Override
    @Transactional
    public Car create(String name) {
        Car car = new Car();
        car.setName(name);
        car.setStatus(CarStatus.NEW);
        return carRepository.save(car);
    }

    @Override
    @Transactional
    public Car assemble(AssembleCarRequestDTO request) {
        Car car = findById(request.carId());

        if (car.isAlreadyAssembled()) {
            throw new CarAssemblyException("Car with id " + request.carId() + " is already assembled.");
        }

        validateComponentsAreDistinct(request);

        car.setEngineId(request.engineId());
        car.setSuspensionId(request.suspensionId());
        car.setAerodynamicsId(request.aerodynamicsId());
        car.setGearboxId(request.gearboxId());
        car.setElectronicsId(request.electronicsId());
        car.setStatus(CarStatus.ASSEMBLED);
        car.setAssembledAt(LocalDateTime.now());

        Car saved = carRepository.save(car);
        publishAssemblyCompleted(saved);
        return saved;
    }

    private void publishAssemblyCompleted(Car car) {
        CarAssemblyCompletedEvent event = new CarAssemblyCompletedEvent(
                UUID.randomUUID().toString(),
                car.getId(),
                car.getAssembledAt().toInstant(ZoneOffset.UTC),
                car.getEngineId(),
                car.getSuspensionId(),
                car.getAerodynamicsId(),
                car.getGearboxId(),
                car.getElectronicsId()
        );

        jmsTemplate.convertAndSend(jmsProperties.carAssemblyQueue(), event);
        log.info("Published CarAssemblyCompletedEvent for car {} to queue {}",
                car.getId(), jmsProperties.carAssemblyQueue());
    }

    private void validateComponentsAreDistinct(AssembleCarRequestDTO request) {
        if (Objects.equals(request.engineId(), request.suspensionId())
                || Objects.equals(request.engineId(), request.aerodynamicsId())
                || Objects.equals(request.engineId(), request.gearboxId())
                || Objects.equals(request.engineId(), request.electronicsId())
                || Objects.equals(request.suspensionId(), request.aerodynamicsId())
                || Objects.equals(request.suspensionId(), request.gearboxId())
                || Objects.equals(request.suspensionId(), request.electronicsId())
                || Objects.equals(request.aerodynamicsId(), request.gearboxId())
                || Objects.equals(request.aerodynamicsId(), request.electronicsId())
                || Objects.equals(request.gearboxId(), request.electronicsId())) {
            throw new CarAssemblyException("Each car component must be unique.");
        }
    }
}
