package team2.race_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RaceNotFoundException extends RuntimeException {
    public RaceNotFoundException(String message) {
        super(message);
    }
}