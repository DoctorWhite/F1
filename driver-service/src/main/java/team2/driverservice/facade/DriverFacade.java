package team2.driverservice.facade;

import team2.driverservice.dto.*;

import java.util.List;

public interface DriverFacade {
    DriverResponseDto createDriver(DriverCreateRequestDto request);

    List<DriverResponseDto> getAllDrivers();

    DriverResponseDto getDriverById(Long id);

    DriverResponseDto updateDriver(Long id, DriverUpdateRequestDto request);

    void deleteDriver(Long id);

    DriverRecommendationDto recommendDriver(RaceConditionsRequestDto request);

    List<DriverRankingEntryDto> rankDrivers(RaceConditionsRequestDto request);

    DriverLineupSuggestionDto suggestRaceLineup(RaceConditionsRequestDto request);
}

