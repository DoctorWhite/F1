package team2.driverservice.model;

public enum DriverRole {
    MAIN(2.5),
    TEST(0.0),
    RESERVE(0.0);

    private final double bonusPoints;

    DriverRole(double bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public double bonusPoints() {
        return bonusPoints;
    }
}