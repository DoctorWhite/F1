package team2.driverservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team2.driverservice.dto.TelemetryBatchRequestDto;
import team2.driverservice.dto.TelemetryRecordDto;
import team2.driverservice.model.ReceivedTelemetryMessage;
import team2.driverservice.repository.ReceivedTelemetryMessageRepository;
import team2.driverservice.service.TelemetryService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryMessageListener {

    private final TelemetryService telemetryService;
    private final JobLauncher jobLauncher;
    private final Job processTelemetryJob;
    private final ReceivedTelemetryMessageRepository messageRepository;

    @Transactional
    @JmsListener(destination = "${driver-service.jms.telemetry-queue}")
    public void onTelemetryEvent(RaceTelemetryEvent event) {
        if (event.messageId() == null || event.messageId().isBlank()) {
            log.warn("Received telemetry event without messageId, skipping");
            return;
        }

        if (messageRepository.existsByMessageId(event.messageId())) {
            log.warn("Duplicate telemetry message {}, skipping", event.messageId());
            return;
        }

        messageRepository.save(ReceivedTelemetryMessage.builder()
                .messageId(event.messageId())
                .driverId(event.driverId())
                .raceId(event.raceId())
                .receivedAt(LocalDateTime.now())
                .build());

        List<TelemetryRecordDto> records = event.readings().stream()
                .map(r -> new TelemetryRecordDto(r.fuelLevel(), r.engineTemp(), r.tirePressure(), r.lapTime()))
                .toList();

        telemetryService.ingestBatch(new TelemetryBatchRequestDto(event.driverId(), event.raceId(), records));

        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(processTelemetryJob, params);
        } catch (Exception e) {
            log.warn("Batch job failed after receiving telemetry event: {}", e.getMessage());
        }
    }
}
