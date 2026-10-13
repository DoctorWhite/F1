package team2.driverservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team2.driverservice.dto.DriverRaceStatsDto;
import team2.driverservice.dto.TelemetryBatchRequestDto;
import team2.driverservice.service.TelemetryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.base-path:/api/v1}/telemetry")
@RequiredArgsConstructor
@Tag(name = "Telemetry", description = "Race telemetry ingestion and batch processing")
public class TelemetryController {

    private final TelemetryService telemetryService;
    private final JobLauncher jobLauncher;
    private final Job processTelemetryJob;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Ingest a batch of telemetry readings for a driver in a race")
    public void ingestTelemetry(@Valid @RequestBody TelemetryBatchRequestDto request) {
        telemetryService.ingestBatch(request);
    }

    @PostMapping("/process")
    @Operation(summary = "Trigger the batch job to process all pending telemetry into race stats")
    public ResponseEntity<Map<String, Object>> processTelemetry() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();
            var execution = jobLauncher.run(processTelemetryJob, params);
            return ResponseEntity.ok(Map.of(
                    "jobExecutionId", execution.getId(),
                    "status", execution.getStatus().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/drivers/{driverId}/stats")
    @Operation(summary = "Get processed race stats for a driver")
    public List<DriverRaceStatsDto> getRaceStats(@PathVariable Long driverId) {
        return telemetryService.getRaceStats(driverId);
    }
}
