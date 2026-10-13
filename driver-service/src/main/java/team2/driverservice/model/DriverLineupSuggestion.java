package team2.driverservice.model;

public record DriverLineupSuggestion(
        RankedDriver primary,
        RankedDriver secondary,
        String rationale
) { }

