package team2.driverservice.service;

import org.springframework.stereotype.Component;
import team2.driverservice.model.Driver;
import team2.driverservice.model.RankedDriver;
import team2.driverservice.model.WeatherCondition;

@Component
public class DriverNarrativeBuilder {

    public String recommendationExplanation(
            Driver driver,
            WeatherCondition weatherCondition,
            int riskLevel,
            int technicalDemand
    ) {
        return "Driver %s %s %s is the best fit for %s conditions due to strong wet skill/consistency balance, risk fit near %d, and feedback quality suited for technical demand %d."
                .formatted(
                        driver.getId(),
                        driver.getFirstName(),
                        driver.getLastName(),
                        weatherCondition.name().toLowerCase(),
                        riskLevel,
                        technicalDemand
                );
    }

    public String lineupRationale(
            RankedDriver primary,
            RankedDriver secondary,
            WeatherCondition weatherCondition
    ) {
        return "Primary seat goes to %s %s (score %.2f) and the second seat to %s %s (score %.2f) based on overall suitability for %s conditions."
                .formatted(
                        primary.driver().getFirstName(),
                        primary.driver().getLastName(),
                        primary.score(),
                        secondary.driver().getFirstName(),
                        secondary.driver().getLastName(),
                        secondary.score(),
                        weatherCondition.name().toLowerCase()
                );
    }
}