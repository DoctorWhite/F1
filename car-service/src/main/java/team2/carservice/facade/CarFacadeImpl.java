package team2.carservice.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team2.carservice.dto.AssembleCarRequestDTO;
import team2.carservice.dto.CarDTO;
import team2.carservice.dto.CreateCarDTO;
import team2.carservice.mapper.CarMapper;
import team2.carservice.service.CarService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CarFacadeImpl implements CarFacade {

    private final CarService carService;
    private final CarMapper carMapper;

    @Override
    public List<CarDTO> findAll() {
        return carService.findAll()
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDTO findById(Long id) {
        return carMapper.toDto(carService.findById(id));
    }

    @Override
    public CarDTO create(CreateCarDTO dto) {
        return carMapper.toDto(carService.create(dto.name()));
    }

    @Override
    public CarDTO assemble(AssembleCarRequestDTO request) {
        return carMapper.toDto(carService.assemble(request));
    }
}