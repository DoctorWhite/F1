package team2.driverservice.exception;

public class NoDriversAvailableException extends RuntimeException {
    public NoDriversAvailableException() {
        super("No drivers are available for evaluation.");
    }
}

