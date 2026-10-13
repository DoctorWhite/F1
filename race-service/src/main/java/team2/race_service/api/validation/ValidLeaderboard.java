package team2.race_service.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RaceResultValidator.class)
@Documented
public @interface ValidLeaderboard {
    String message() default "Invalid leaderboard: drivers must be unique and positions sequential";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}