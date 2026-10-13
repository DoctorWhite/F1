package team2.race_service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import team2.race_service.api.*;
import team2.race_service.data.entity.DriverLap;
import team2.race_service.data.entity.Race;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.data.enums.Weather;
import team2.race_service.mapper.RaceMapper;
import team2.race_service.service.RaceService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaceFacadeImplTest {

    @Mock
    private RaceService raceService;

    @Mock
    private RaceMapper raceMapper;

    @InjectMocks
    private RaceFacadeImpl raceFacade;

    @Test
    void findById_raceExists_returnsMappedRaceViewDto() {
        Race race = new Race(1L, "Monaco", LocalDate.now(), Weather.SUNNY, null);
        RaceViewDto dto = new RaceViewDto(1L, "Monaco", LocalDate.now(), Weather.SUNNY, null, new ArrayList<>());

        when(raceService.getRaceById(1L)).thenReturn(race);
        when(raceMapper.toViewDto(race)).thenReturn(dto);

        RaceViewDto result = raceFacade.findById(1L);

        assertEquals(dto, result);
        verify(raceService).getRaceById(1L);
        verify(raceMapper).toViewDto(race);
    }

    @Test
    void findAll_pageableProvided_returnsPagedMappedDtos() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().plusDays(1);

        Race race1 = new Race(1L, "Monaco", date1, Weather.SUNNY, null);
        Race race2 = new Race(2L, "Monza", date2, Weather.CLOUDY, null);

        RaceViewDto dto1 = new RaceViewDto(1L, "Monaco", date1, Weather.SUNNY, null, new ArrayList<>());
        RaceViewDto dto2 = new RaceViewDto(2L, "Monza", date2, Weather.CLOUDY, null, new ArrayList<>());

        Pageable pageable = PageRequest.of(0, 10);

        when(raceService.getAllRaces()).thenReturn(List.of(race1, race2));
        when(raceMapper.toViewDto(race1)).thenReturn(dto1);
        when(raceMapper.toViewDto(race2)).thenReturn(dto2);

        Page<RaceViewDto> result = raceFacade.findAll(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(raceService).getAllRaces();
        verify(raceMapper).toViewDto(race1);
        verify(raceMapper).toViewDto(race2);
    }

    @Test
    void createRace_validDto_returnsMappedSavedRaceViewDto() {
        LocalDate date = LocalDate.now().plusDays(3);
        CreateRaceDto createDto = new CreateRaceDto("Silverstone", date, Weather.RAINY);
        Race entityToSave = new Race(null, "Silverstone", date, Weather.RAINY, null);
        Race savedRace = new Race(1L, "Silverstone", date, Weather.RAINY, null);
        RaceViewDto responseDto = new RaceViewDto(1L, "Silverstone", date, Weather.RAINY, null, new ArrayList<>());

        when(raceMapper.toEntity(createDto)).thenReturn(entityToSave);
        when(raceService.createRace(entityToSave)).thenReturn(savedRace);
        when(raceMapper.toViewDto(savedRace)).thenReturn(responseDto);

        RaceViewDto result = raceFacade.createRace(createDto);

        assertEquals(responseDto, result);
        verify(raceMapper).toEntity(createDto);
        verify(raceService).createRace(entityToSave);
        verify(raceMapper).toViewDto(savedRace);
    }

    @Test
    void assignResult_idsMatch_returnsMappedUpdatedRaceViewDto() {
        LocalDate date = LocalDate.now().minusDays(1);
        Driver winner = new Driver(1L, "Charles", "Leclerc");
        RaceResult raceResult = new RaceResult(1L, Map.of(1, winner), 1L);
        AssignRaceResultDto dto = new AssignRaceResultDto(1L, raceResult);

        Race updatedRace = new Race(1L, "Monza", date, Weather.SUNNY, raceResult, new ArrayList<>());
        RaceViewDto responseDto = new RaceViewDto(1L, "Monza", date, Weather.SUNNY, raceResult, new ArrayList<>());

        when(raceService.assignResultToRace(1L, raceResult)).thenReturn(updatedRace);
        when(raceMapper.toViewDto(updatedRace)).thenReturn(responseDto);

        RaceViewDto result = raceFacade.assignResult(1L, dto);

        assertEquals(responseDto, result);
        verify(raceService).assignResultToRace(1L, raceResult);
        verify(raceMapper).toViewDto(updatedRace);
    }

    @Test
    void assignResult_idsDoNotMatch_throwsIllegalArgumentException() {
        Driver winner = new Driver(1L, "Charles", "Leclerc");
        RaceResult raceResult = new RaceResult(1L, Map.of(1, winner), 1L);
        AssignRaceResultDto dto = new AssignRaceResultDto(2L, raceResult);

        assertThrows(IllegalArgumentException.class, () -> raceFacade.assignResult(1L, dto));

        verify(raceService, never()).assignResultToRace(anyLong(), any());
        verify(raceMapper, never()).toViewDto(any());
    }

    @Test
    void updateRace_validDto_returnsMappedUpdatedRaceViewDto() {
        LocalDate date = LocalDate.now().plusDays(5);
        UpdateRaceDto updateDto = new UpdateRaceDto("Spa", date, Weather.CLOUDY);

        DriverLap existingLap = new DriverLap();
        Race existingRace = new Race(1L, "OldLocation", LocalDate.now(), Weather.SUNNY, null,
                new ArrayList<>(List.of(existingLap)));
        Race updatedRace = new Race(1L, "Spa", date, Weather.CLOUDY, null,
                new ArrayList<>(List.of(existingLap)));
        RaceViewDto responseDto = new RaceViewDto(1L, "Spa", date, Weather.CLOUDY, null, new ArrayList<>());

        when(raceService.getRaceById(1L)).thenReturn(existingRace);
        when(raceService.updateRace(1L, existingRace)).thenReturn(updatedRace);
        when(raceMapper.toViewDto(updatedRace)).thenReturn(responseDto);

        RaceViewDto result = raceFacade.updateRace(1L, updateDto);

        assertEquals(responseDto, result);
        verify(raceService).getRaceById(1L);
        verify(raceService).updateRace(1L, existingRace);
        verify(raceMapper).toViewDto(updatedRace);
        assertEquals(1, existingRace.getDriverLaps().size());
    }

    @Test
    void deleteRace_existingId_delegatesToService() {
        assertDoesNotThrow(() -> raceFacade.deleteRace(1L));

        verify(raceService).deleteRace(1L);
    }

    @Test
    void getRacePoints_pointsCalculated_returnsRacePointsViewDto() {
        Driver driver = new Driver(1L, "Max", "Verstappen");
        Map<Driver, Integer> points = Map.of(driver, 25);

        when(raceService.calculateRacePoints(1L)).thenReturn(points);

        RacePointsViewDto result = raceFacade.getRacePoints(1L);

        assertEquals(points, result.racePoints());
        verify(raceService).calculateRacePoints(1L);
    }
}