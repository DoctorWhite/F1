package team2.carservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import team2.carservice.entity.Car;
import team2.carservice.entity.CarStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
    }

    @Test
    void findByStatus_returnsOnlyCarsWithGivenStatus() {
        Car newCar = new Car(null, "Ferrari F1-24", null, null, null, null, null, CarStatus.NEW, null);
        Car assembledCar = new Car(null, "McLaren MCL38", "eng-1", "sus-1", "aero-1", "gb-1", "elec-1",
                CarStatus.ASSEMBLED, LocalDateTime.now());

        carRepository.save(newCar);
        carRepository.save(assembledCar);

        List<Car> result = carRepository.findByStatus(CarStatus.ASSEMBLED);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("McLaren MCL38");
        assertThat(result.getFirst().getStatus()).isEqualTo(CarStatus.ASSEMBLED);
    }

    @Test
    void findByAssembledAtIsNotNull_returnsOnlyAssembledCars() {
        Car newCar = new Car(null, "Ferrari F1-24", null, null, null, null, null, CarStatus.NEW, null);
        Car assembledCar1 = new Car(null, "McLaren MCL38", "eng-1", "sus-1", "aero-1", "gb-1", "elec-1",
                CarStatus.ASSEMBLED, LocalDateTime.now());
        Car assembledCar2 = new Car(null, "Mercedes W15", "eng-2", "sus-2", "aero-2", "gb-2", "elec-2",
                CarStatus.ASSEMBLED, LocalDateTime.now().minusHours(1));

        carRepository.save(newCar);
        carRepository.save(assembledCar1);
        carRepository.save(assembledCar2);

        List<Car> result = carRepository.findByAssembledAtIsNotNull();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Car::getName)
                .containsExactlyInAnyOrder("McLaren MCL38", "Mercedes W15");
    }
}