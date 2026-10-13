package team2.driverservice.model;

public record DriverRecommendation(
        Driver driver,
        double suitabilityScore,
        WeatherCondition weatherCondition,
        String explanation
) { }

