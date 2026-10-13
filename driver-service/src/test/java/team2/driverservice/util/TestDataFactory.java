package team2.driverservice.util;

import team2.driverservice.dto.DriverCreateRequestDto;
import team2.driverservice.dto.DriverLineupSuggestionDto;
import team2.driverservice.dto.DriverRankingEntryDto;
import team2.driverservice.dto.DriverRecommendationDto;
import team2.driverservice.dto.DriverResponseDto;
import team2.driverservice.dto.DriverUpdateRequestDto;
import team2.driverservice.dto.RaceConditionsRequestDto;
import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverRole;
import team2.driverservice.model.WeatherCondition;

public final class TestDataFactory {

    public static final DriverResponseDto DRIVER_RESPONSE_LEWIS = new DriverResponseDto(
            1L, "Lewis", "Hamilton", "United Kingdom", DriverRole.MAIN,
            92, 84, 96, 95);

    public static final DriverResponseDto DRIVER_RESPONSE_CHARLES = new DriverResponseDto(
            2L, "Charles", "Leclerc", "Monaco", DriverRole.MAIN,
            88, 90, 91, 89);

    private TestDataFactory() {
    }

    public static DriverCreateRequestDto driverCreateRequest() {
        return new DriverCreateRequestDto(
                "Lewis",
                "Hamilton",
                "United Kingdom",
                DriverRole.MAIN,
                92,
                84,
                96,
                95
        );
    }

    public static DriverUpdateRequestDto driverUpdateRequest() {
        return new DriverUpdateRequestDto(
                "Lewis",
                "Hamilton",
                "United Kingdom",
                DriverRole.MAIN,
                93,
                85,
                97,
                96
        );
    }

    public static Driver driverWithoutId() {
        return Driver.builder()
                .firstName("Lewis")
                .lastName("Hamilton")
                .nationality("United Kingdom")
                .role(DriverRole.MAIN)
                .wetSkill(92)
                .aggressiveness(84)
                .consistency(96)
                .feedbackQuality(95)
                .build();
    }

    public static Driver driverWithId(Long id) {
        Driver driver = driverWithoutId();
        driver.setId(id);
        return driver;
    }

    /** Same profile as {@link #DRIVER_RESPONSE_CHARLES}, id assigned on save. */
    public static Driver driverCharlesWithoutId() {
        return Driver.builder()
                .firstName("Charles")
                .lastName("Leclerc")
                .nationality("Monaco")
                .role(DriverRole.MAIN)
                .wetSkill(88)
                .aggressiveness(90)
                .consistency(91)
                .feedbackQuality(89)
                .build();
    }

    public static RaceConditionsRequestDto wetRaceConditions() {
        return new RaceConditionsRequestDto(WeatherCondition.WET, 50, 50);
    }

    public static Driver lowWetSkillDriver(Long id) {
        return new Driver(id, "A", "Alpha", "X", DriverRole.MAIN, 10, 50, 50, 50);
    }

    public static Driver highWetSkillDriver(Long id) {
        return new Driver(id, "B", "Beta", "Y", DriverRole.MAIN, 95, 50, 50, 50);
    }

    public static DriverRecommendationDto driverRecommendationDto() {
        return new DriverRecommendationDto(
                DRIVER_RESPONSE_LEWIS,
                98.5,
                WeatherCondition.WET,
                "Strong wet-weather package."
        );
    }

    public static DriverRankingEntryDto driverRankingEntryFirst() {
        return new DriverRankingEntryDto(DRIVER_RESPONSE_LEWIS, 98.5, 1);
    }

    public static DriverLineupSuggestionDto driverLineupSuggestionDto() {
        return new DriverLineupSuggestionDto(
                DRIVER_RESPONSE_LEWIS,
                DRIVER_RESPONSE_CHARLES,
                "Primary and secondary based on wet pace."
        );
    }
}
