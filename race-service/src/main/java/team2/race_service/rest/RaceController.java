package team2.race_service.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.race_service.api.*;
import team2.race_service.facade.RaceFacade;

@RestController
@RequestMapping(path = "/races")
@RequiredArgsConstructor
@Tag(name = "Race Management", description = "Endpoints for scheduling and managing F1 races")
public class RaceController {

    private final RaceFacade raceFacade;

    @Operation(summary = "Get race details by ID")
    @GetMapping(path = "/{id}")
    public ResponseEntity<RaceViewDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(raceFacade.findById(id));
    }

    @Operation(summary = "List all races with pagination")
    @GetMapping
    public ResponseEntity<Page<RaceViewDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(raceFacade.findAll(pageable));
    }

    @Operation(summary = "Get race points by Race ID")
    @GetMapping("/{id}/points")
    public ResponseEntity<RacePointsViewDto> getRacePoints(@PathVariable Long id) {
        return ResponseEntity.ok(raceFacade.getRacePoints(id));
    }

    @Operation(summary = "Schedule a new race")
    @PostMapping
    public ResponseEntity<RaceViewDto> createRace(@Valid @RequestBody CreateRaceDto race) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceFacade.createRace(race));
    }

    @Operation(summary = "Update race details (location, date, or weather)")
    @PutMapping(path = "/{id}")
    public ResponseEntity<RaceViewDto> updateRace(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRaceDto raceUpdate) {
        return ResponseEntity.ok(raceFacade.updateRace(id, raceUpdate));
    }

    @Operation(summary = "Update race laps content")
    @PutMapping(path = "/{id}/laps")
    public ResponseEntity<RaceViewDto> addLapToRace(
            @PathVariable Long id,
            @Valid @RequestBody DriverLapDto lapDto) {
        return ResponseEntity.ok(raceFacade.addLapToRace(id, lapDto));
    }

    @Operation(summary = "Delete a race from the schedule")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRace(@PathVariable("id") Long id) {
        raceFacade.deleteRace(id);
    }

    @Operation(
            summary = "Assign official race results",
            description = "Updates an existing race with the final leaderboard. Validates that the race date has passed."
    )
    @PatchMapping(path = "/{id}/results")
    public ResponseEntity<RaceViewDto> assignResult(
            @PathVariable Long id,
            @Valid @RequestBody AssignRaceResultDto resultDto) {
        RaceViewDto updatedRace = raceFacade.assignResult(id, resultDto);
        return ResponseEntity.ok(updatedRace);
    }
}