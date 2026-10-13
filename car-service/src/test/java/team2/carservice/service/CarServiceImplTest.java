package team2.carservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import team2.carservice.config.JmsProperties;
import team2.carservice.dto.AssembleCarRequestDTO;
import team2.carservice.entity.Car;
import team2.carservice.entity.CarStatus;
import team2.carservice.exception.CarAssemblyException;
import team2.carservice.exception.CarNotFoundException;
import team2.carservice.messaging.CarAssemblyCompletedEvent;
import team2.carservice.repository.CarRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private JmsProperties jmsProperties;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        lenient().when(jmsProperties.carAssemblyQueue()).thenReturn("car.assembly.completed");
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    void findAll_carsExist_returnsAllCars() {
        // Arrange
        List<Car> cars = List.of(newCar(1L, "Ferrari F1-24"), newCar(2L, "Red Bull RB20"));
        when(carRepository.findAll()).thenReturn(cars);

        // Act
        List<Car> result = carService.findAll();

        // Assert
        assertThat(result).isEqualTo(cars);
        verify(carRepository).findAll();
    }

    @Test
    void findAll_noCars_returnsEmptyList() {
        // Arrange
        when(carRepository.findAll()).thenReturn(List.of());

        // Act
        List<Car> result = carService.findAll();

        // Assert
        assertThat(result).isEmpty();
        verify(carRepository).findAll();
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    void findById_carExists_returnsCar() {
        // Arrange
        Car car = newCar(1L, "Ferrari F1-24");
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        // Act
        Car result = carService.findById(1L);

        // Assert
        assertThat(result).isEqualTo(car);
        verify(carRepository).findById(1L);
    }

    @Test
    void findById_carNotFound_throwsCarNotFoundException() {
        // Arrange
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> carService.findById(99L))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    void create_validName_savesCarWithNewStatus() {
        // Arrange
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Car result = carService.create("McLaren MCL38");

        // Assert
        assertThat(result.getName()).isEqualTo("McLaren MCL38");
        assertThat(result.getStatus()).isEqualTo(CarStatus.NEW);
        assertThat(result.getId()).isNull();
    }

    @Test
    void create_validName_delegatesSaveToRepository() {
        // Arrange
        Car saved = newCar(1L, "Mercedes W15");
        when(carRepository.save(any(Car.class))).thenReturn(saved);

        // Act
        Car result = carService.create("Mercedes W15");

        // Assert
        assertThat(result).isEqualTo(saved);
        verify(carRepository).save(any(Car.class));
    }

    // -------------------------------------------------------------------------
    // assemble
    // -------------------------------------------------------------------------

    @Test
    void assemble_validRequest_setsAllComponentsAndAssembledStatus() {
        // Arrange
        Car car = newCar(1L, "Ferrari F1-24");
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Car result = carService.assemble(validAssembleRequest(1L));

        // Assert
        assertThat(result.getStatus()).isEqualTo(CarStatus.ASSEMBLED);
        assertThat(result.getEngineId()).isEqualTo("engine-1");
        assertThat(result.getSuspensionId()).isEqualTo("suspension-1");
        assertThat(result.getAerodynamicsId()).isEqualTo("aero-1");
        assertThat(result.getGearboxId()).isEqualTo("gearbox-1");
        assertThat(result.getElectronicsId()).isEqualTo("electronics-1");
        assertThat(result.getAssembledAt()).isNotNull();
    }

    @Test
    void assemble_validRequest_setsAssembledAtToNow() {
        // Arrange
        Car car = newCar(1L, "Ferrari F1-24");
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // Act
        Car result = carService.assemble(validAssembleRequest(1L));

        // Assert
        assertThat(result.getAssembledAt()).isAfterOrEqualTo(before);
    }

    @Test
    void assemble_validRequest_publishesCarAssemblyCompletedEvent() {
        Car car = newCar(1L, "Ferrari F1-24");
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> {
            Car c = inv.getArgument(0);
            if (c.getId() == null) {
                c.setId(1L);
            }
            return c;
        });

        carService.assemble(validAssembleRequest(1L));

        ArgumentCaptor<CarAssemblyCompletedEvent> eventCaptor =
                ArgumentCaptor.forClass(CarAssemblyCompletedEvent.class);
        verify(jmsTemplate).convertAndSend(eq("car.assembly.completed"), eventCaptor.capture());
        CarAssemblyCompletedEvent published = eventCaptor.getValue();
        assertThat(published.carId()).isEqualTo(1L);
        assertThat(published.engineId()).isEqualTo("engine-1");
        assertThat(published.messageId()).isNotBlank();
        assertThat(published.assembledAt())
                .isEqualTo(car.getAssembledAt().toInstant(ZoneOffset.UTC));
    }

    @Test
    void assemble_carNotFound_throwsCarNotFoundException() {
        // Arrange
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> carService.assemble(validAssembleRequest(99L)))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("99");

        verify(carRepository, never()).save(any());
    }

    @Test
    void assemble_alreadyAssembledCar_throwsCarAssemblyException() {
        // Arrange
        Car assembled = assembledCar(1L, "Ferrari F1-24");
        when(carRepository.findById(1L)).thenReturn(Optional.of(assembled));

        // Act & Assert
        assertThatThrownBy(() -> carService.assemble(validAssembleRequest(1L)))
                .isInstanceOf(CarAssemblyException.class)
                .hasMessageContaining("already assembled");

        verify(carRepository, never()).save(any());
    }

    @Test
    void assemble_allDuplicateComponents_throwsCarAssemblyException() {
        // Arrange
        Car car = newCar(1L, "Ferrari F1-24");
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        AssembleCarRequestDTO request = new AssembleCarRequestDTO(
                1L, "same", "same", "same", "same", "same"
        );

        // Act & Assert
        assertThatThrownBy(() -> carService.assemble(request))
                .isInstanceOf(CarAssemblyException.class)
                .hasMessageContaining("unique");
    }

    @Test
    void assemble_validRequest_saveIsCalledOnce() {
        // Arrange
        Car car = newCar(1L, "Ferrari F1-24");
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        carService.assemble(validAssembleRequest(1L));

        // Assert
        verify(carRepository).save(any(Car.class));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Car newCar(Long id, String name) {
        Car car = new Car();
        car.setId(id);
        car.setName(name);
        car.setStatus(CarStatus.NEW);
        return car;
    }

    private Car assembledCar(Long id, String name) {
        Car car = new Car();
        car.setId(id);
        car.setName(name);
        car.setStatus(CarStatus.ASSEMBLED);
        car.setEngineId("engine-1");
        car.setSuspensionId("suspension-1");
        car.setAerodynamicsId("aero-1");
        car.setGearboxId("gearbox-1");
        car.setElectronicsId("electronics-1");
        car.setAssembledAt(LocalDateTime.now().minusDays(1));
        return car;
    }

    private AssembleCarRequestDTO validAssembleRequest(Long carId) {
        return new AssembleCarRequestDTO(
                carId,
                "engine-1",
                "suspension-1",
                "aero-1",
                "gearbox-1",
                "electronics-1"
        );
    }
}
