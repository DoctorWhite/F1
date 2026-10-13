package team2.race_service.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Valid;
import team2.race_service.api.Driver;
import team2.race_service.data.entity.RaceResult;

import java.util.Map;

public class RaceResultValidator implements ConstraintValidator<ValidLeaderboard, RaceResult> {

    @Override
    public boolean isValid(RaceResult result, ConstraintValidatorContext context) {
        if (result == null || result.getLeaderboard() == null) {
            return true;
        }

        var leaderboard = result.getLeaderboard();

        if (hasDuplicatedDrivers(leaderboard)) return false;

        if (hasNonSequentialPositions(leaderboard)) return false;

        return !isMissingFastestLapDriverInResults(result, context, leaderboard);
    }

    private boolean isMissingFastestLapDriverInResults(RaceResult result, ConstraintValidatorContext context, Map<Integer, @Valid Driver> leaderboard) {
        if (result.getFastestLapDriverId() != null) {
            boolean driverExists = leaderboard.values().stream()
                    .anyMatch(d -> d.getId().equals(result.getFastestLapDriverId()));
            if (!driverExists) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Fastest lap driver ID must exist in the leaderboard")
                        .addPropertyNode("fastestLapDriverId")
                        .addConstraintViolation();
                return true;
            }
        }
        return false;
    }

    private boolean hasNonSequentialPositions(Map<Integer, @Valid Driver> leaderboard) {
        int size = leaderboard.size();
        for (int i = 1; i <= size; i++) {
            if (!leaderboard.containsKey(i)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDuplicatedDrivers(Map<Integer, @Valid Driver> leaderboard) {
        long uniqueDrivers = leaderboard.values().stream().distinct().count();
        return uniqueDrivers != leaderboard.size();
    }
}