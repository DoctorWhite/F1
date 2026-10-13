package team2.carservice.service;

import team2.carservice.dto.AssembleCarRequestDTO;
import team2.carservice.entity.Car;

import java.util.List;

public interface CarService {

    List<Car> findAll();

    Car findById(Long id);

    Car create(String name);

    Car assemble(AssembleCarRequestDTO request);
}