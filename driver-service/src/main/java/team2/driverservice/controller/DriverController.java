package team2.driverservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team2.driverservice.dto.*;
import team2.driverservice.facade.DriverFacade;

import java.util.List;

@RestController
@RequestMapping("${api.base-path:/api/v1}/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Service", description = "Operations for managing the Formula 1 driver roster")
public class DriverController {

    private final DriverFacade driverFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a driver")
    public DriverResponseDto createDriver(@Valid @RequestBody DriverCreateRequestDto request) {
        return driverFacade.createDriver(request);
    }

    @GetMapping
    @Operation(summary = "Get all drivers")
    public List<DriverResponseDto> getAllDrivers() {
        return driverFacade.getAllDrivers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by id")
    public DriverResponseDto getDriverById(@PathVariable Long id) {
        return driverFacade.getDriverById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update driver")
    public DriverResponseDto updateDriver(@PathVariable Long id, @Valid @RequestBody DriverUpdateRequestDto request) {
        return driverFacade.updateDriver(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete driver")
    public void deleteDriver(@PathVariable Long id) {
        driverFacade.deleteDriver(id);
    }

    @PostMapping("/recommendation")
    @Operation(summary = "Recommend the best driver for race conditions")
    public DriverRecommendationDto recommendDriver(@Valid @RequestBody RaceConditionsRequestDto request) {
        return driverFacade.recommendDriver(request);
    }

    @PostMapping("/rankings")
    @Operation(summary = "Rank all drivers for race conditions")
    public List<DriverRankingEntryDto> rankDrivers(@Valid @RequestBody RaceConditionsRequestDto request) {
        return driverFacade.rankDrivers(request);
    }

    @PostMapping("/lineup-suggestions")
    @Operation(summary = "Suggest the best two-driver race lineup")
    public DriverLineupSuggestionDto suggestRaceLineup(@Valid @RequestBody RaceConditionsRequestDto request) {
        return driverFacade.suggestRaceLineup(request);
    }
}
