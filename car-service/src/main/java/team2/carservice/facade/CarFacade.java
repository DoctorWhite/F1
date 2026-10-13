package team2.carservice.facade;

import team2.carservice.dto.AssembleCarRequestDTO;
import team2.carservice.dto.CarDTO;
import team2.carservice.dto.CreateCarDTO;

import java.util.List;

public interface CarFacade {

    List<CarDTO> findAll();

    CarDTO findById(Long id);

    CarDTO create(CreateCarDTO dto);

    CarDTO assemble(AssembleCarRequestDTO request);
}