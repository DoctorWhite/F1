package team2.driverservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.driverservice.config.DriverServiceProperties;
import team2.driverservice.exception.DriverNotFoundException;
import team2.driverservice.exception.NoDriversAvailableException;
import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverLineupSuggestion;
import team2.driverservice.model.DriverRecommendation;
import team2.driverservice.model.DriverRole;
import team2.driverservice.model.WeatherCondition;
import team2.driverservice.repository.DriverRepository;
import team2.driverservice.util.TestDataFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverServiceProperties properties;

    @Mock
    private DriverNarrativeBuilder narrativeBuilder;

    @InjectMocks
    private DriverServiceImpl driverService;

    @BeforeEach
    void setUpDefaultProperties() {
        lenient().when(properties.getWetWeatherWeight()).thenReturn(0.45);
        lenient().when(properties.getDryWeatherWeight()).thenReturn(0.20);
        lenient().when(properties.getMixedWeatherWeight()).thenReturn(0.30);
        lenient().when(properties.getConsistencyWeight()).thenReturn(0.30);
        lenient().when(properties.getAggressivenessWeight()).thenReturn(0.15);
        lenient().when(properties.getFeedbackWeight()).thenReturn(0.10);
        lenient().when(properties.getTechnicalDemandFeedbackBonus()).thenReturn(0.20);
    }

    @Test
    void createDriver_validInput_returnsSavedDriver() {
        // Arrange
        Driver input = TestDataFactory.driverWithoutId();
        Driver saved = TestDataFactory.driverWithId(1L);
        when(driverRepository.save(any(Driver.class))).thenReturn(saved);

        // Act
        Driver result = driverService.createDriver(input);

        // Assert
        assertThat(result).isEqualTo(saved);
        verify(driverRepository).save(input);
    }

    @Test
    void getAllDrivers_driversExist_returnsAll() {
        // Arrange
        List<Driver> drivers = List.of(
                TestDataFactory.driverWithId(1L),
                TestDataFactory.driverWithId(2L));
        when(driverRepository.findAll()).thenReturn(drivers);

        // Act
        List<Driver> result = driverService.getAllDrivers();

        // Assert
        assertThat(result).isEqualTo(drivers);
        verify(driverRepository).findAll();
    }

    @Test
    void getDriverById_driverFound_returnsDriver() {
        // Arrange
        Driver driver = TestDataFactory.driverWithId(1L);
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        // Act
        Driver result = driverService.getDriverById(1L);

        // Assert
        assertThat(result).isEqualTo(driver);
    }

    @Test
    void getDriverById_driverNotFound_throwsDriverNotFoundException() {
        // Arrange
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> driverService.getDriverById(99L))
                .isInstanceOf(DriverNotFoundException.class);
    }

    @Test
    void updateDriver_driverExists_returnsUpdatedDriver() {
        // Arrange
        Driver update = TestDataFactory.driverWithoutId();
        Driver expected = TestDataFactory.driverWithId(1L);
        when(driverRepository.existsById(1L)).thenReturn(true);
        when(driverRepository.save(any(Driver.class))).thenReturn(expected);

        // Act
        Driver result = driverService.updateDriver(1L, update);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(driverRepository).existsById(1L);
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void updateDriver_driverNotFound_throwsDriverNotFoundException() {
        // Arrange
        when(driverRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> driverService.updateDriver(1L, TestDataFactory.driverWithoutId()))
                .isInstanceOf(DriverNotFoundException.class);
    }

    @Test
    void deleteDriver_driverExists_deletes() {
        // Arrange
        when(driverRepository.existsById(1L)).thenReturn(true);

        // Act
        driverService.deleteDriver(1L);

        // Assert
        verify(driverRepository).deleteById(1L);
    }

    @Test
    void deleteDriver_driverNotFound_throwsDriverNotFoundException() {
        // Arrange
        when(driverRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> driverService.deleteDriver(1L))
                .isInstanceOf(DriverNotFoundException.class);
    }

    @Test
    void recommendDriver_wetConditions_returnsDriverWithHighestWetSkill() {
        // Arrange
        Driver weaker = TestDataFactory.lowWetSkillDriver(1L);
        Driver stronger = TestDataFactory.highWetSkillDriver(2L);
        when(driverRepository.findAll()).thenReturn(List.of(weaker, stronger));
        when(narrativeBuilder.recommendationExplanation(any(), any(), any(int.class), any(int.class)))
                .thenReturn("explanation");

        // Act
        DriverRecommendation rec = driverService.recommendDriver(WeatherCondition.WET, 50, 50);

        // Assert
        assertThat(rec.driver().getWetSkill()).isEqualTo(95);
        assertThat(rec.weatherCondition()).isEqualTo(WeatherCondition.WET);
    }

    @Test
    void recommendDriver_noDrivers_throwsNoDriversAvailableException() {
        // Arrange
        when(driverRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> driverService.recommendDriver(WeatherCondition.WET, 50, 50))
                .isInstanceOf(NoDriversAvailableException.class);
    }

    @Test
    void rankDrivers_driversExist_returnsOrderedList() {
        // Arrange
        when(driverRepository.findAll()).thenReturn(List.of(
                TestDataFactory.lowWetSkillDriver(1L),
                TestDataFactory.highWetSkillDriver(2L)));

        // Act
        var ranked = driverService.rankDrivers(WeatherCondition.WET, 50, 50);

        // Assert
        assertThat(ranked).hasSize(2);
        assertThat(ranked.getFirst().driver().getWetSkill()).isGreaterThanOrEqualTo(ranked.get(1).driver().getWetSkill());
    }

    @Test
    void rankDrivers_noDrivers_throwsNoDriversAvailableException() {
        // Arrange
        when(driverRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> driverService.rankDrivers(WeatherCondition.DRY, 50, 50))
                .isInstanceOf(NoDriversAvailableException.class);
    }

    @Test
    void suggestRaceLineup_twoMainDrivers_returnsLineup() {
        // Arrange
        Driver first = new Driver(1L, "A", "Aa", "X", DriverRole.MAIN, 80, 50, 50, 50);
        Driver second = new Driver(2L, "B", "Bb", "Y", DriverRole.MAIN, 70, 50, 50, 50);
        when(driverRepository.findAll()).thenReturn(List.of(first, second));
        when(narrativeBuilder.lineupRationale(any(), any(), any())).thenReturn("rationale");

        // Act
        DriverLineupSuggestion lineup = driverService.suggestRaceLineup(WeatherCondition.WET, 50, 50);

        // Assert
        assertThat(lineup.primary().driver().getId()).isNotNull();
        assertThat(lineup.secondary().driver().getId()).isNotNull();
        assertThat(lineup.rationale()).isEqualTo("rationale");
    }
}
