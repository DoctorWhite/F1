package team2.driverservice.messaging;

import java.util.List;

public record RaceTelemetryEvent(String messageId, Long driverId, Long raceId, List<Reading> readings) {

    public record Reading(double fuelLevel, double engineTemp, double tirePressure, double lapTime) {
    }
}
