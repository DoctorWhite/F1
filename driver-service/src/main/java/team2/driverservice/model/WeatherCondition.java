package team2.driverservice.model;

import team2.driverservice.config.DriverServiceProperties;

public enum WeatherCondition {
    DRY {
        @Override
        public double weight(DriverServiceProperties properties) {
            return properties.getDryWeatherWeight();
        }

        @Override
        public double weatherComponent(Driver driver, double weatherWeight) {
            return driver.getConsistency() * weatherWeight;
        }
    },
    MIXED {
        @Override
        public double weight(DriverServiceProperties properties) {
            return properties.getMixedWeatherWeight();
        }

        @Override
        public double weatherComponent(Driver driver, double weatherWeight) {
            return ((driver.getWetSkill() + driver.getConsistency()) / MIXED_AVERAGE_DIVISOR) * weatherWeight;
        }
    },
    WET {
        @Override
        public double weight(DriverServiceProperties properties) {
            return properties.getWetWeatherWeight();
        }

        @Override
        public double weatherComponent(Driver driver, double weatherWeight) {
            return driver.getWetSkill() * weatherWeight;
        }
    };

    private static final double MIXED_AVERAGE_DIVISOR = 2.0;

    public abstract double weight(DriverServiceProperties properties);

    public abstract double weatherComponent(Driver driver, double weatherWeight);
}
