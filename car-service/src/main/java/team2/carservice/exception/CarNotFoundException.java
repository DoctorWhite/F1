package team2.carservice.exception;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(Long id) {
        super("Car with id " + id + " was not found.");
    }
}