package team2.driverservice.service;

import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverLineupSuggestion;
import team2.driverservice.model.DriverRecommendation;
import team2.driverservice.model.RankedDriver;
import team2.driverservice.model.WeatherCondition;

import java.util.List;

public interface DriverService {
    Driver createDriver(Driver driver);

    List<Driver> getAllDrivers();

    Driver getDriverById(Long id);

    Driver updateDriver(Long id, Driver driver);

    void deleteDriver(Long id);

    DriverRecommendation recommendDriver(WeatherCondition weatherCondition, int riskLevel, int technicalDemand);

    List<RankedDriver> rankDrivers(WeatherCondition weatherCondition, int riskLevel, int technicalDemand);

    DriverLineupSuggestion suggestRaceLineup(WeatherCondition weatherCondition, int riskLevel, int technicalDemand);
}