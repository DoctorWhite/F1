package team2.race_service.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import team2.race_service.api.*;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.data.enums.Weather;
import team2.race_service.facade.RaceFacade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RaceControllerTest {

    @Mock
    private RaceFacade raceFacade;

    @InjectMocks
    private RaceController raceController;

    @Test
    void findById_existingId_returnsOkResponseWithRaceViewDto() {
        RaceViewDto dto = new RaceViewDto(1L, "Monaco", LocalDate.now(), Weather.SUNNY, null, new ArrayList<>());
        when(raceFacade.findById(1L)).thenReturn(dto);

        ResponseEntity<RaceViewDto> response = raceController.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(raceFacade).findById(1L);
    }

    @Test
    void findAll_racesExist_returnsOkResponseWithPagedRaces() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RaceViewDto> page = new PageImpl<>(List.of(
                new RaceViewDto(1L, "Monaco", LocalDate.now(), Weather.SUNNY, null, new ArrayList<>()),
                new RaceViewDto(2L, "Monza", LocalDate.now().plusDays(1), Weather.CLOUDY, null, new ArrayList<>())
        ));

        when(raceFacade.findAll(pageable)).thenReturn(page);

        ResponseEntity<Page<RaceViewDto>> response = raceController.findAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(raceFacade).findAll(pageable);
    }

    @Test
    void getRacePoints_existingId_returnsOkResponseWithRacePoints() {
        Driver driver = new Driver(1L, "Max", "Verstappen");
        RacePointsViewDto dto = new RacePointsViewDto(Map.of(driver, 25));

        when(raceFacade.getRacePoints(1L)).thenReturn(dto);

        ResponseEntity<RacePointsViewDto> response = raceController.getRacePoints(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(raceFacade).getRacePoints(1L);
    }

    @Test
    void createRace_validDto_returnsCreatedResponseWithRaceViewDto() {
        LocalDate date = LocalDate.now().plusDays(7);
        CreateRaceDto createDto = new CreateRaceDto("Silverstone", date, Weather.RAINY);
        RaceViewDto responseDto = new RaceViewDto(1L, "Silverstone", date, Weather.RAINY, null, new ArrayList<>());

        when(raceFacade.createRace(createDto)).thenReturn(responseDto);

        ResponseEntity<RaceViewDto> response = raceController.createRace(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(raceFacade).createRace(createDto);
    }

    @Test
    void updateRace_validDto_returnsOkResponseWithUpdatedRaceViewDto() {
        LocalDate date = LocalDate.now().plusDays(5);
        UpdateRaceDto updateDto = new UpdateRaceDto("Spa", date, Weather.CLOUDY);
        RaceViewDto responseDto = new RaceViewDto(1L, "Spa", date, Weather.CLOUDY, null, new ArrayList<>());

        when(raceFacade.updateRace(1L, updateDto)).thenReturn(responseDto);

        ResponseEntity<RaceViewDto> response = raceController.updateRace(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(raceFacade).updateRace(1L, updateDto);
    }

    @Test
    void deleteRace_existingId_delegatesToFacade() {
        assertDoesNotThrow(() -> raceController.deleteRace(1L));

        verify(raceFacade).deleteRace(1L);
    }

    @Test
    void assignResult_validDto_returnsOkResponseWithUpdatedRaceViewDto() {
        LocalDate date = LocalDate.now().minusDays(1);
        Driver winner = new Driver(1L, "Max", "Verstappen");
        RaceResult raceResult = new RaceResult(1L, Map.of(1, winner), 1L);
        AssignRaceResultDto requestDto = new AssignRaceResultDto(1L, raceResult);
        RaceViewDto responseDto = new RaceViewDto(1L, "Monza", date, Weather.SUNNY, raceResult, new ArrayList<>());

        when(raceFacade.assignResult(1L, requestDto)).thenReturn(responseDto);

        ResponseEntity<RaceViewDto> response = raceController.assignResult(1L, requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(raceFacade).assignResult(1L, requestDto);
    }
}