package team2.driverservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverRole;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByRole(DriverRole role);

    List<Driver> findByNationality(String nationality);
}
