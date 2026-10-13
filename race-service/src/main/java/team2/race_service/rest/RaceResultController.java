package team2.race_service.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.race_service.api.RaceResultViewDto;
import team2.race_service.facade.RaceResultFacade;

@RestController
@RequestMapping(path = "/race-results")
@RequiredArgsConstructor
@Tag(name = "Race Results Management", description = "Endpoints for managing race results")
public class RaceResultController {

    private final RaceResultFacade raceResultFacade;

    @GetMapping
    @Operation(summary = "Get all race results", description = "Returns a paginated list of all race results")
    public ResponseEntity<Page<RaceResultViewDto>> getAllResults(Pageable pageable) {
        return ResponseEntity.ok(raceResultFacade.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get race result by ID", description = "Returns a single race result matching the given ID")
    public ResponseEntity<RaceResultViewDto> getResultById(@PathVariable Long id) {
        return ResponseEntity.ok(raceResultFacade.findById(id));
    }

    @GetMapping("/fastest-lap/{driverId}")
    @Operation(summary = "Get results by fastest lap driver", description = "Returns paginated race results where the specified driver had the fastest lap")
    public ResponseEntity<Page<RaceResultViewDto>> getByFastestLapDriver(
            @PathVariable Long driverId,
            Pageable pageable) {
        return ResponseEntity.ok(raceResultFacade.findRaceResultsForFastestLapDriverId(pageable, driverId));
    }
}