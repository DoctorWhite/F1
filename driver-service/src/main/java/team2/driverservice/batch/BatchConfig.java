package team2.driverservice.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.ChunkOrientedStepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import team2.driverservice.model.DriverTelemetry;
import team2.driverservice.repository.DriverTelemetryRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final DriverTelemetryRepository telemetryRepository;
    private final TelemetryItemProcessor telemetryItemProcessor;
    private final TelemetryStatsWriter telemetryStatsWriter;

    @Value("${driver-service.batch.chunk-size:10}")
    private int chunkSize;

    @Bean
    public Job processTelemetryJob(JobRepository jobRepository, Step processTelemetryStep) {
        return new JobBuilder("processTelemetryJob", jobRepository)
                .start(processTelemetryStep)
                .build();
    }

    @Bean
    public Step processTelemetryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                     ItemReader<DriverTelemetry> telemetryItemReader) {
        return new ChunkOrientedStepBuilder<DriverTelemetry, DriverTelemetry>("processTelemetryStep", jobRepository, chunkSize)
                .reader(telemetryItemReader)
                .processor(telemetryItemProcessor)
                .writer(telemetryStatsWriter)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<DriverTelemetry> telemetryItemReader() {
        List<DriverTelemetry> items = new ArrayList<>(telemetryRepository.findByProcessedFalse());
        return () -> items.isEmpty() ? null : items.removeFirst();
    }
}
