package team2.carservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.carservice.dto.AssembleCarRequestDTO;
import team2.carservice.dto.CarDTO;
import team2.carservice.dto.CreateCarDTO;
import team2.carservice.entity.Car;
import team2.carservice.entity.CarStatus;
import team2.carservice.mapper.CarMapper;
import team2.carservice.service.CarService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarFacadeImplTest {

    @Mock
    private CarService carService;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarFacadeImpl carFacade;

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    void findAll_carsExist_returnsMappedDtos() {
        // Arrange
        Car car1 = newCar(1L, "Ferrari F1-24");
        Car car2 = newCar(2L, "Red Bull RB20");
        CarDTO dto1 = newCarDto(1L, "Ferrari F1-24");
        CarDTO dto2 = newCarDto(2L, "Red Bull RB20");

        when(carService.findAll()).thenReturn(List.of(car1, car2));
        when(carMapper.toDto(car1)).thenReturn(dto1);
        when(carMapper.toDto(car2)).thenReturn(dto2);

        // Act
        List<CarDTO> result = carFacade.findAll();

        // Assert
        assertThat(result).containsExactly(dto1, dto2);
        verify(carService).findAll();
        verify(carMapper).toDto(car1);
        verify(carMapper).toDto(car2);
    }

    @Test
    void findAll_noCars_returnsEmptyList() {
        // Arrange
        when(carService.findAll()).thenReturn(List.of());

        // Act
        List<CarDTO> result = carFacade.findAll();

        // Assert
        assertThat(result).isEmpty();
        verify(carService).findAll();
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    void findById_carExists_returnsMappedDto() {
        // Arrange
        Car car = newCar(1L, "Ferrari F1-24");
        CarDTO dto = newCarDto(1L, "Ferrari F1-24");

        when(carService.findById(1L)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(dto);

        // Act
        CarDTO result = carFacade.findById(1L);

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(carService).findById(1L);
        verify(carMapper).toDto(car);
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    void create_validDto_delegatesToServiceAndMapsResult() {
        // Arrange
        CreateCarDTO createDto = new CreateCarDTO("McLaren MCL38");
        Car created = newCar(3L, "McLaren MCL38");
        CarDTO dto = newCarDto(3L, "McLaren MCL38");

        when(carService.create("McLaren MCL38")).thenReturn(created);
        when(carMapper.toDto(created)).thenReturn(dto);

        // Act
        CarDTO result = carFacade.create(createDto);

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(carService).create("McLaren MCL38");
        verify(carMapper).toDto(created);
    }

    // -------------------------------------------------------------------------
    // assemble
    // -------------------------------------------------------------------------

    @Test
    void assemble_validRequest_delegatesToServiceAndMapsResult() {
        // Arrange
        AssembleCarRequestDTO request = validAssembleRequest(1L);
        Car assembled = assembledCar(1L, "Ferrari F1-24");
        CarDTO dto = assembledCarDto(1L, "Ferrari F1-24");

        when(carService.assemble(request)).thenReturn(assembled);
        when(carMapper.toDto(assembled)).thenReturn(dto);

        // Act
        CarDTO result = carFacade.assemble(request);

        // Assert
        assertThat(result).isEqualTo(dto);
        assertThat(result.status()).isEqualTo(CarStatus.ASSEMBLED);
        verify(carService).assemble(request);
        verify(carMapper).toDto(assembled);
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
        car.setAssembledAt(LocalDateTime.now());
        return car;
    }

    private CarDTO newCarDto(Long id, String name) {
        return new CarDTO(id, name, null, null, null, null, null, CarStatus.NEW, null);
    }

    private CarDTO assembledCarDto(Long id, String name) {
        return new CarDTO(id, name, "engine-1", "suspension-1", "aero-1", "gearbox-1", "electronics-1",
                CarStatus.ASSEMBLED, LocalDateTime.now());
    }

    private AssembleCarRequestDTO validAssembleRequest(Long carId) {
        return new AssembleCarRequestDTO(carId, "engine-1", "suspension-1", "aero-1", "gearbox-1", "electronics-1");
    }
}
