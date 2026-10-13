package team2.carservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.carservice.dto.AssembleCarRequestDTO;
import team2.carservice.dto.CarDTO;
import team2.carservice.dto.CreateCarDTO;
import team2.carservice.facade.CarFacade;

import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
@Tag(name = "Car Management", description = "Endpoints for managing F1 cars and their assembly. Base path: /api/v1")
public class CarController {

    private final CarFacade carFacade;

    @Operation(summary = "List all cars")
    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars() {
        return ResponseEntity.ok(carFacade.findAll());
    }

    @Operation(summary = "Create a new car", description = "Server assigns the car ID.")
    @PostMapping
    public ResponseEntity<CarDTO> createCar(@Valid @RequestBody CreateCarDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carFacade.create(request));
    }

    @Operation(summary = "Get car details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carFacade.findById(id));
    }

    @Operation(summary = "Assemble a car", description = "Assigns components (engine, suspension, aerodynamics, gearbox, electronics) to an existing car.")
    @PostMapping("/assemble")
    public ResponseEntity<CarDTO> assembleCar(@Valid @RequestBody AssembleCarRequestDTO request) {
        return ResponseEntity.ok(carFacade.assemble(request));
    }
}