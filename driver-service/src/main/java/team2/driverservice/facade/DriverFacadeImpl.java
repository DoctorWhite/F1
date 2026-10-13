package team2.driverservice.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team2.driverservice.dto.*;
import team2.driverservice.mapper.DriverMapper;
import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverLineupSuggestion;
import team2.driverservice.model.DriverRecommendation;
import team2.driverservice.model.RankedDriver;
import team2.driverservice.service.DriverService;

import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DriverFacadeImpl implements DriverFacade {

    private final DriverService driverService;
    private final DriverMapper driverMapper;

    @Override
    public DriverResponseDto createDriver(DriverCreateRequestDto request) {
        Driver createdDriver = driverService.createDriver(driverMapper.toDriver(request));
        return driverMapper.toResponseDto(createdDriver);
    }

    @Override
    public List<DriverResponseDto> getAllDrivers() {
        return driverService.getAllDrivers().stream()
                .map(driverMapper::toResponseDto)
                .toList();
    }

    @Override
    public DriverResponseDto getDriverById(Long id) {
        return driverMapper.toResponseDto(driverService.getDriverById(id));
    }

    @Override
    public DriverResponseDto updateDriver(Long id, DriverUpdateRequestDto request) {
        Driver updatedDriver = driverService.updateDriver(id, driverMapper.toDriver(id, request));
        return driverMapper.toResponseDto(updatedDriver);
    }

    @Override
    public void deleteDriver(Long id) {
        driverService.deleteDriver(id);
    }

    @Override
    public DriverRecommendationDto recommendDriver(RaceConditionsRequestDto request) {
        DriverRecommendation recommendation = driverService.recommendDriver(
                request.weatherCondition(), request.riskLevel(), request.technicalDemand());
        return new DriverRecommendationDto(
                driverMapper.toResponseDto(recommendation.driver()),
                recommendation.suitabilityScore(),
                recommendation.weatherCondition(),
                recommendation.explanation()
        );
    }

    @Override
    public List<DriverRankingEntryDto> rankDrivers(RaceConditionsRequestDto request) {
        List<RankedDriver> rankedDrivers = driverService.rankDrivers(
                request.weatherCondition(), request.riskLevel(), request.technicalDemand());
        return IntStream.range(0, rankedDrivers.size())
                .mapToObj(index -> driverMapper.toRankingEntry(rankedDrivers.get(index), index + 1))
                .toList();
    }

    @Override
    public DriverLineupSuggestionDto suggestRaceLineup(RaceConditionsRequestDto request) {
        DriverLineupSuggestion suggestion = driverService.suggestRaceLineup(
                request.weatherCondition(), request.riskLevel(), request.technicalDemand());
        return new DriverLineupSuggestionDto(
                driverMapper.toResponseDto(suggestion.primary().driver()),
                driverMapper.toResponseDto(suggestion.secondary().driver()),
                suggestion.rationale()
        );
    }
}
