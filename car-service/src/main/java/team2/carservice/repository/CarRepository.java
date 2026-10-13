package team2.carservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.carservice.entity.Car;
import team2.carservice.entity.CarStatus;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByStatus(CarStatus status);

    List<Car> findByAssembledAtIsNotNull();
}