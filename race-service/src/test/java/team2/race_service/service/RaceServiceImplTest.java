package team2.race_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.race_service.config.PointsConfig;
import team2.race_service.api.Driver;
import team2.race_service.data.entity.Race;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.data.enums.Weather;
import team2.race_service.data.repository.RaceRepository;
import team2.race_service.exception.RaceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaceServiceImplTest {

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private PointsConfig pointsConfig;

    @InjectMocks
    private RaceServiceImpl raceService;

    @Test
    void getRaceById_raceExists_returnsRace() {
        Race race = new Race(1L, "Monaco", LocalDate.now(), Weather.SUNNY, null);
        when(raceRepository.findById(1L)).thenReturn(Optional.of(race));

        Race result = raceService.getRaceById(1L);

        assertEquals(race, result);
        verify(raceRepository).findById(1L);
    }

    @Test
    void getRaceById_raceDoesNotExist_throwsRaceNotFoundException() {
        when(raceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RaceNotFoundException.class, () -> raceService.getRaceById(1L));

        verify(raceRepository).findById(1L);
    }

    @Test
    void getAllRaces_repositoryContainsRaces_returnsRaceList() {
        List<Race> races = List.of(
                new Race(1L, "Monaco", LocalDate.now(), Weather.SUNNY, null),
                new Race(2L, "Monza", LocalDate.now().plusDays(1), Weather.CLOUDY, null)
        );
        when(raceRepository.findAll()).thenReturn(races);

        List<Race> result = raceService.getAllRaces();

        assertEquals(races, result);
        verify(raceRepository).findAll();
    }

    @Test
    void createRace_validRace_returnsSavedRace() {
        Race raceToCreate = new Race(null, "Silverstone", LocalDate.now().plusDays(3), Weather.RAINY, null);
        Race savedRace = new Race(1L, "Silverstone", LocalDate.now().plusDays(3), Weather.RAINY, null);

        when(raceRepository.save(raceToCreate)).thenReturn(savedRace);

        Race result = raceService.createRace(raceToCreate);

        assertEquals(savedRace, result);
        verify(raceRepository).save(raceToCreate);
    }

    @Test
    void updateRace_raceExists_returnsUpdatedRaceWithProvidedId() {
        LocalDate date = LocalDate.now().plusDays(10);
        Race newRace = new Race(999L, "Spa", date, Weather.CLOUDY, null);
        Race savedRace = new Race(1L, "Spa", date, Weather.CLOUDY, null);

        when(raceRepository.existsById(1L)).thenReturn(true);
        when(raceRepository.save(any(Race.class))).thenReturn(savedRace);

        Race result = raceService.updateRace(1L, newRace);

        assertEquals(savedRace, result);
        verify(raceRepository).existsById(1L);
        verify(raceRepository).save(new Race(1L, "Spa", date, Weather.CLOUDY, null));
    }

    @Test
    void updateRace_raceDoesNotExist_throwsRaceNotFoundException() {
        Race newRace = new Race(null, "Spa", LocalDate.now().plusDays(10), Weather.CLOUDY, null);
        when(raceRepository.existsById(1L)).thenReturn(false);

        assertThrows(RaceNotFoundException.class, () -> raceService.updateRace(1L, newRace));

        verify(raceRepository).existsById(1L);
        verify(raceRepository, never()).save(any());
    }

    @Test
    void deleteRace_raceExists_deletesRace() {
        when(raceRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> raceService.deleteRace(1L));

        verify(raceRepository).existsById(1L);
        verify(raceRepository).deleteById(1L);
    }

    @Test
    void deleteRace_raceDoesNotExist_throwsRaceNotFoundException() {
        when(raceRepository.existsById(1L)).thenReturn(false);

        assertThrows(RaceNotFoundException.class, () -> raceService.deleteRace(1L));

        verify(raceRepository).existsById(1L);
        verify(raceRepository, never()).deleteById(anyLong());
    }

    @Test
    void assignResultToRace_raceAlreadyHappened_returnsUpdatedRace() {
        LocalDate date = LocalDate.now().minusDays(1);
        Driver winner = new Driver(1L, "Max", "Verstappen");
        RaceResult raceResult = new RaceResult(1L, Map.of(1, winner), 1L);
        Race existingRace = new Race(1L, "Monza", date, Weather.SUNNY, null);
        Race updatedRace = new Race(1L, "Monza", date, Weather.SUNNY, raceResult);

        when(raceRepository.findById(1L)).thenReturn(Optional.of(existingRace));
        when(raceRepository.save(any(Race.class))).thenReturn(updatedRace);

        Race result = raceService.assignResultToRace(1L, raceResult);

        assertEquals(updatedRace, result);
        verify(raceRepository).findById(1L);
        verify(raceRepository).save(new Race(1L, "Monza", date, Weather.SUNNY, raceResult));
    }

    @Test
    void assignResultToRace_raceIsInFuture_throwsIllegalStateException() {
        Driver winner = new Driver(1L, "Max", "Verstappen");
        RaceResult raceResult = new RaceResult(1L, Map.of(1, winner), 1L);
        Race futureRace = new Race(1L, "Melbourne", LocalDate.now().plusDays(5), Weather.SUNNY, null);

        when(raceRepository.findById(1L)).thenReturn(Optional.of(futureRace));

        assertThrows(IllegalStateException.class, () -> raceService.assignResultToRace(1L, raceResult));

        verify(raceRepository).findById(1L);
        verify(raceRepository, never()).save(any());
    }

    @Test
    void calculateRacePoints_raceHasNoResults_throwsIllegalStateException() {
        Race race = new Race(1L, "Bahrain", LocalDate.now().minusDays(1), Weather.SUNNY, null);
        when(raceRepository.findById(1L)).thenReturn(Optional.of(race));

        assertThrows(IllegalStateException.class, () -> raceService.calculateRacePoints(1L));

        verify(raceRepository).findById(1L);
    }

    @Test
    void calculateRacePoints_fastestLapEligible_addsBonusPoints() {
        Driver driver1 = new Driver(1L, "Max", "Verstappen");
        Driver driver2 = new Driver(2L, "Lando", "Norris");

        RaceResult raceResult = new RaceResult(1L, Map.of(1, driver1, 2, driver2), 2L);
        Race race = new Race(1L, "Imola", LocalDate.now().minusDays(1), Weather.CLOUDY, raceResult);

        PointsConfig.FastestLap fastestLap = new PointsConfig.FastestLap();
        fastestLap.setBonus(1);
        fastestLap.setMinPosition(10);

        when(raceRepository.findById(1L)).thenReturn(Optional.of(race));
        when(pointsConfig.getPointsForPosition(1)).thenReturn(25);
        when(pointsConfig.getPointsForPosition(2)).thenReturn(18);
        when(pointsConfig.getFastestLap()).thenReturn(fastestLap);

        Map<Driver, Integer> result = raceService.calculateRacePoints(1L);

        assertEquals(25, result.get(driver1));
        assertEquals(19, result.get(driver2));
        verify(raceRepository).findById(1L);
        verify(pointsConfig).getPointsForPosition(1);
        verify(pointsConfig).getPointsForPosition(2);
        verify(pointsConfig).getFastestLap();
    }

    @Test
    void calculateRacePoints_fastestLapNotEligible_doesNotAddBonusPoints() {
        Driver driver1 = new Driver(1L, "Max", "Verstappen");
        Driver driver11 = new Driver(11L, "Pierre", "Gasly");

        RaceResult raceResult = new RaceResult(1L, Map.of(1, driver1, 11, driver11), 11L);
        Race race = new Race(1L, "Suzuka", LocalDate.now().minusDays(1), Weather.SUNNY, raceResult);

        PointsConfig.FastestLap fastestLap = new PointsConfig.FastestLap();
        fastestLap.setBonus(1);
        fastestLap.setMinPosition(10);

        when(raceRepository.findById(1L)).thenReturn(Optional.of(race));
        when(pointsConfig.getPointsForPosition(1)).thenReturn(25);
        when(pointsConfig.getPointsForPosition(11)).thenReturn(0);
        when(pointsConfig.getFastestLap()).thenReturn(fastestLap);

        Map<Driver, Integer> result = raceService.calculateRacePoints(1L);

        assertEquals(25, result.get(driver1));
        assertEquals(0, result.get(driver11));
        verify(raceRepository).findById(1L);
        verify(pointsConfig).getFastestLap();
    }

    @Test
    void assignResultToRace_raceDoesNotExist_throwsRaceNotFoundException() {
        when(raceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RaceNotFoundException.class, () -> raceService.assignResultToRace(1L, mock(RaceResult.class)));

        verify(raceRepository).findById(1L);
        verify(raceRepository, never()).save(any());
    }

    @Test
    void calculateRacePoints_raceDoesNotExist_throwsRaceNotFoundException() {
        when(raceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RaceNotFoundException.class, () -> raceService.calculateRacePoints(1L));

        verify(raceRepository).findById(1L);
        verifyNoInteractions(pointsConfig);
    }
}