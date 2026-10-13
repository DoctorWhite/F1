package team2.driverservice.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import team2.driverservice.config.TestSecurityConfig;
import team2.driverservice.dto.TelemetryBatchRequestDto;
import team2.driverservice.dto.TelemetryRecordDto;
import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverRaceStats;
import team2.driverservice.model.DriverRole;
import team2.driverservice.repository.DriverRaceStatsRepository;
import team2.driverservice.repository.DriverRepository;
import team2.driverservice.repository.DriverTelemetryRepository;
import team2.driverservice.service.TelemetryService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestSecurityConfig.class)
class TelemetryBatchJobIT {

    @Autowired
    private TelemetryService telemetryService;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job processTelemetryJob;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private DriverTelemetryRepository telemetryRepository;
    @Autowired
    private DriverRaceStatsRepository raceStatsRepository;

    private Driver driver;

    @BeforeEach
    void setUp() {
        raceStatsRepository.deleteAll();
        telemetryRepository.deleteAll();
        driverRepository.deleteAll();

        driver = driverRepository.save(Driver.builder()
                .firstName("Max").lastName("Verstappen").nationality("Dutch")
                .role(DriverRole.MAIN)
                .wetSkill(85).aggressiveness(80).consistency(90).feedbackQuality(88)
                .build());
    }

    @Test
    void ingestAndProcess_computesStatsAndMarksAllRecordsProcessed() throws Exception {
        // Ingest three telemetry readings — lap times 91, 92, 93 (avg 92, low std dev)
        telemetryService.ingestBatch(new TelemetryBatchRequestDto(
                driver.getId(), 1L,
                List.of(
                        new TelemetryRecordDto(82.0, 104.0, 24.5, 91.0),
                        new TelemetryRecordDto(78.0, 106.0, 24.3, 92.0),
                        new TelemetryRecordDto(74.0, 105.0, 24.4, 93.0)
                )
        ));
        assertThat(telemetryRepository.findByProcessedFalse()).hasSize(3);

        runJob();

        // All records should now be marked processed
        assertThat(telemetryRepository.findByProcessedFalse()).isEmpty();

        // One race stats entry should exist for this driver + race
        List<DriverRaceStats> stats = raceStatsRepository.findByDriverId(driver.getId());
        assertThat(stats).hasSize(1);

        DriverRaceStats raceStat = stats.getFirst();
        assertThat(raceStat.getRaceId()).isEqualTo(1L);
        assertThat(raceStat.getSampleCount()).isEqualTo(3);
        assertThat(raceStat.getAvgLapTime()).isEqualTo(92.0);
        assertThat(raceStat.getAvgFuelLevel()).isEqualTo(78.0);
        assertThat(raceStat.getDerivedConsistency()).isGreaterThan(0).isLessThanOrEqualTo(100);
    }

    @Test
    void process_multipleRaces_createsOneStatsEntryPerRace() throws Exception {
        telemetryService.ingestBatch(new TelemetryBatchRequestDto(
                driver.getId(), 1L,
                List.of(new TelemetryRecordDto(80.0, 105.0, 24.5, 92.0))
        ));
        telemetryService.ingestBatch(new TelemetryBatchRequestDto(
                driver.getId(), 2L,
                List.of(new TelemetryRecordDto(75.0, 108.0, 24.1, 88.0))
        ));

        runJob();

        List<DriverRaceStats> stats = raceStatsRepository.findByDriverId(driver.getId());
        assertThat(stats).hasSize(2);
        assertThat(stats).extracting(DriverRaceStats::getRaceId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void process_noUnprocessedRecords_jobCompletesWithNoStatsCreated() throws Exception {
        runJob();

        assertThat(raceStatsRepository.findAll()).isEmpty();
    }

    private void runJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(processTelemetryJob, params);
    }
}
