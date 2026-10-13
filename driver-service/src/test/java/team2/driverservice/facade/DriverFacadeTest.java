package team2.driverservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.driverservice.dto.DriverLineupSuggestionDto;
import team2.driverservice.dto.DriverRankingEntryDto;
import team2.driverservice.dto.DriverResponseDto;
import team2.driverservice.mapper.DriverMapper;
import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverLineupSuggestion;
import team2.driverservice.model.DriverRecommendation;
import team2.driverservice.model.RankedDriver;
import team2.driverservice.model.WeatherCondition;
import team2.driverservice.service.DriverService;
import team2.driverservice.util.TestDataFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class DriverFacadeTest {

    @Mock
    private DriverService driverService;

    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverFacadeImpl driverFacade;

    @Test
    void createDriver_validRequest_returnsResponseDto() {
        // Arrange
        Driver mapped = TestDataFactory.driverWithoutId();
        Driver saved = TestDataFactory.driverWithId(1L);
        Mockito.when(driverMapper.toDriver(any())).thenReturn(mapped);
        Mockito.when(driverService.createDriver(mapped)).thenReturn(saved);
        Mockito.when(driverMapper.toResponseDto(saved)).thenReturn(TestDataFactory.DRIVER_RESPONSE_LEWIS);

        // Act
        DriverResponseDto found = driverFacade.createDriver(TestDataFactory.driverCreateRequest());

        // Assert
        assertThat(found).isEqualTo(TestDataFactory.DRIVER_RESPONSE_LEWIS);
    }

    @Test
    void getAllDrivers_driversExist_returnsMappedList() {
        // Arrange
        Driver d1 = TestDataFactory.driverWithId(1L);
        Driver d2 = TestDataFactory.driverWithId(2L);
        Mockito.when(driverService.getAllDrivers()).thenReturn(List.of(d1, d2));
        Mockito.when(driverMapper.toResponseDto(d1)).thenReturn(TestDataFactory.DRIVER_RESPONSE_LEWIS);
        Mockito.when(driverMapper.toResponseDto(d2)).thenReturn(TestDataFactory.DRIVER_RESPONSE_CHARLES);

        // Act
        List<DriverResponseDto> result = driverFacade.getAllDrivers();

        // Assert
        assertThat(result).containsExactly(
                TestDataFactory.DRIVER_RESPONSE_LEWIS,
                TestDataFactory.DRIVER_RESPONSE_CHARLES);
    }

    @Test
    void getDriverById_driverFound_returnsResponseDto() {
        // Arrange
        Driver driver = TestDataFactory.driverWithId(1L);
        Mockito.when(driverService.getDriverById(1L)).thenReturn(driver);
        Mockito.when(driverMapper.toResponseDto(driver)).thenReturn(TestDataFactory.DRIVER_RESPONSE_LEWIS);

        // Act
        DriverResponseDto found = driverFacade.getDriverById(1L);

        // Assert
        assertThat(found).isEqualTo(TestDataFactory.DRIVER_RESPONSE_LEWIS);
    }

    @Test
    void updateDriver_validRequest_returnsResponseDto() {
        // Arrange
        Driver mapped = TestDataFactory.driverWithId(1L);
        Mockito.when(driverMapper.toDriver(eq(1L), any())).thenReturn(mapped);
        Mockito.when(driverService.updateDriver(1L, mapped)).thenReturn(mapped);
        Mockito.when(driverMapper.toResponseDto(mapped)).thenReturn(TestDataFactory.DRIVER_RESPONSE_LEWIS);

        // Act
        DriverResponseDto found = driverFacade.updateDriver(1L, TestDataFactory.driverUpdateRequest());

        // Assert
        assertThat(found).isEqualTo(TestDataFactory.DRIVER_RESPONSE_LEWIS);
    }

    @Test
    void deleteDriver_existingId_delegatesToService() {
        // Act
        driverFacade.deleteDriver(1L);

        // Assert
        Mockito.verify(driverService, Mockito.times(1)).deleteDriver(1L);
    }

    @Test
    void recommendDriver_wetConditions_returnsRecommendationDto() {
        // Arrange
        Driver driver = TestDataFactory.highWetSkillDriver(2L);
        DriverRecommendation domain = new DriverRecommendation(
                driver,
                99.0,
                WeatherCondition.WET,
                "because wet");
        Mockito.when(driverService.recommendDriver(WeatherCondition.WET, 50, 50)).thenReturn(domain);
        Mockito.when(driverMapper.toResponseDto(driver)).thenReturn(TestDataFactory.DRIVER_RESPONSE_CHARLES);

        // Act
        var result = driverFacade.recommendDriver(TestDataFactory.wetRaceConditions());

        // Assert
        assertThat(result.recommendedDriver()).isEqualTo(TestDataFactory.DRIVER_RESPONSE_CHARLES);
        assertThat(result.suitabilityScore()).isEqualTo(99.0);
        assertThat(result.weatherCondition()).isEqualTo(WeatherCondition.WET);
        assertThat(result.explanation()).isEqualTo("because wet");
    }

    @Test
    void rankDrivers_driversExist_returnsEntriesWithPositions() {
        // Arrange
        RankedDriver r1 = new RankedDriver(TestDataFactory.driverWithId(1L), 100.0);
        RankedDriver r2 = new RankedDriver(TestDataFactory.driverWithId(2L), 90.0);
        DriverRankingEntryDto entry1 = new DriverRankingEntryDto(TestDataFactory.DRIVER_RESPONSE_LEWIS, 100.0, 1);
        DriverRankingEntryDto entry2 = new DriverRankingEntryDto(TestDataFactory.DRIVER_RESPONSE_CHARLES, 90.0, 2);
        Mockito.when(driverService.rankDrivers(WeatherCondition.WET, 50, 50)).thenReturn(List.of(r1, r2));
        Mockito.when(driverMapper.toRankingEntry(r1, 1)).thenReturn(entry1);
        Mockito.when(driverMapper.toRankingEntry(r2, 2)).thenReturn(entry2);

        // Act
        List<DriverRankingEntryDto> result = driverFacade.rankDrivers(TestDataFactory.wetRaceConditions());

        // Assert
        assertThat(result).containsExactly(entry1, entry2);
    }

    @Test
    void suggestRaceLineup_validConditions_returnsLineupDto() {
        // Arrange
        RankedDriver primary = new RankedDriver(TestDataFactory.driverWithId(1L), 100.0);
        RankedDriver secondary = new RankedDriver(TestDataFactory.driverWithId(2L), 90.0);
        DriverLineupSuggestion domain = new DriverLineupSuggestion(primary, secondary, "rationale");
        Mockito.when(driverService.suggestRaceLineup(WeatherCondition.WET, 50, 50)).thenReturn(domain);
        Mockito.when(driverMapper.toResponseDto(primary.driver())).thenReturn(TestDataFactory.DRIVER_RESPONSE_LEWIS);
        Mockito.when(driverMapper.toResponseDto(secondary.driver())).thenReturn(TestDataFactory.DRIVER_RESPONSE_CHARLES);

        // Act
        DriverLineupSuggestionDto result = driverFacade.suggestRaceLineup(TestDataFactory.wetRaceConditions());

        // Assert
        assertThat(result.primaryDriver()).isEqualTo(TestDataFactory.DRIVER_RESPONSE_LEWIS);
        assertThat(result.secondaryDriver()).isEqualTo(TestDataFactory.DRIVER_RESPONSE_CHARLES);
        assertThat(result.rationale()).isEqualTo("rationale");
    }
}
