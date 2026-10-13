package team2.driverservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.driverservice.config.DriverServiceProperties;
import team2.driverservice.exception.DriverNotFoundException;
import team2.driverservice.exception.NoDriversAvailableException;
import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverLineupSuggestion;
import team2.driverservice.model.DriverRecommendation;
import team2.driverservice.model.DriverRole;
import team2.driverservice.model.RankedDriver;
import team2.driverservice.model.WeatherCondition;
import team2.driverservice.repository.DriverRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private static final int LINEUP_SIZE = 2;

    private final DriverRepository driverRepository;
    private final DriverServiceProperties properties;
    private final DriverNarrativeBuilder narrativeBuilder;

    @Override
    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));
    }

    @Override
    public Driver updateDriver(Long id, Driver driver) {
        if (!driverRepository.existsById(id)) {
            throw new DriverNotFoundException(id);
        }
        driver.setId(id);
        return driverRepository.save(driver);
    }

    @Override
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new DriverNotFoundException(id);
        }
        driverRepository.deleteById(id);
    }

    @Override
    public DriverRecommendation recommendDriver(WeatherCondition weatherCondition, int riskLevel, int technicalDemand) {
        RankedDriver rankedDriver = getRankedDrivers(weatherCondition, riskLevel, technicalDemand).stream()
                .findFirst()
                .orElseThrow(NoDriversAvailableException::new);

        return new DriverRecommendation(
                rankedDriver.driver(),
                rankedDriver.score(),
                weatherCondition,
                narrativeBuilder.recommendationExplanation(rankedDriver.driver(), weatherCondition, riskLevel, technicalDemand)
        );
    }

    @Override
    public List<RankedDriver> rankDrivers(WeatherCondition weatherCondition, int riskLevel, int technicalDemand) {
        List<RankedDriver> rankedDrivers = getRankedDrivers(weatherCondition, riskLevel, technicalDemand);
        if (rankedDrivers.isEmpty()) {
            throw new NoDriversAvailableException();
        }
        return rankedDrivers;
    }

    @Override
    public DriverLineupSuggestion suggestRaceLineup(WeatherCondition weatherCondition, int riskLevel, int technicalDemand) {
        List<RankedDriver> allRanked = getRankedDrivers(weatherCondition, riskLevel, technicalDemand);

        List<RankedDriver> eligibleDrivers = allRanked.stream()
                .filter(rankedDriver -> rankedDriver.driver().getRole() != DriverRole.TEST)
                .limit(LINEUP_SIZE)
                .toList();

        if (eligibleDrivers.size() < LINEUP_SIZE) {
            eligibleDrivers = allRanked.stream()
                    .limit(LINEUP_SIZE)
                    .toList();
        }

        if (eligibleDrivers.size() < LINEUP_SIZE) {
            throw new NoDriversAvailableException();
        }

        return new DriverLineupSuggestion(
                eligibleDrivers.get(0),
                eligibleDrivers.get(1),
                narrativeBuilder.lineupRationale(eligibleDrivers.get(0), eligibleDrivers.get(1), weatherCondition)
        );
    }

    private double calculateSuitabilityScore(Driver driver, WeatherCondition weatherCondition, int riskLevel, int technicalDemand) {
        double weatherWeight = weatherCondition.weight(properties);
        double weatherComponent = weatherCondition.weatherComponent(driver, weatherWeight);

        double consistencyComponent = driver.getConsistency() * properties.getConsistencyWeight();
        double riskFit = (100 - Math.abs(driver.getAggressiveness() - riskLevel)) * properties.getAggressivenessWeight();
        double technicalComponent = ((driver.getFeedbackQuality() * technicalDemand) / 100.0) * properties.getTechnicalDemandFeedbackBonus();
        double feedbackComponent = driver.getFeedbackQuality() * properties.getFeedbackWeight();
        double mainDriverBonus = driver.getRole().bonusPoints();

        return round(weatherComponent + consistencyComponent + riskFit + technicalComponent + feedbackComponent + mainDriverBonus);
    }

    private List<RankedDriver> getRankedDrivers(WeatherCondition weatherCondition, int riskLevel, int technicalDemand) {
        return driverRepository.findAll().stream()
                .map(driver -> new RankedDriver(driver, calculateSuitabilityScore(driver, weatherCondition, riskLevel, technicalDemand)))
                .sorted(Comparator.comparingDouble(RankedDriver::score).reversed()
                        .thenComparing(rankedDriver -> rankedDriver.driver().getLastName()))
                .toList();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

