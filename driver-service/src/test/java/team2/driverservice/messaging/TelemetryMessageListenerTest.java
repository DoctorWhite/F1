package team2.driverservice.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import team2.driverservice.dto.TelemetryBatchRequestDto;
import team2.driverservice.model.ReceivedTelemetryMessage;
import team2.driverservice.repository.ReceivedTelemetryMessageRepository;
import team2.driverservice.service.TelemetryService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelemetryMessageListenerTest {

    @Mock
    private TelemetryService telemetryService;
    @Mock
    private JobLauncher jobLauncher;
    @Mock
    private Job processTelemetryJob;
    @Mock
    private ReceivedTelemetryMessageRepository messageRepository;
    @InjectMocks
    private TelemetryMessageListener listener;

    @Test
    void onTelemetryEvent_validEvent_persistsAndIngestsAndTriggersBatchJob() throws Exception {
        RaceTelemetryEvent event = new RaceTelemetryEvent(
                "msg-001", 1L, 1L,
                List.of(
                        new RaceTelemetryEvent.Reading(80.0, 105.0, 24.5, 92.0),
                        new RaceTelemetryEvent.Reading(78.0, 106.0, 24.3, 91.0)
                )
        );
        when(messageRepository.existsByMessageId("msg-001")).thenReturn(false);

        listener.onTelemetryEvent(event);

        verify(messageRepository).save(any(ReceivedTelemetryMessage.class));

        ArgumentCaptor<TelemetryBatchRequestDto> captor = ArgumentCaptor.forClass(TelemetryBatchRequestDto.class);
        verify(telemetryService).ingestBatch(captor.capture());

        TelemetryBatchRequestDto ingested = captor.getValue();
        assertThat(ingested.driverId()).isEqualTo(1L);
        assertThat(ingested.raceId()).isEqualTo(1L);
        assertThat(ingested.readings()).hasSize(2);
        assertThat(ingested.readings().get(0).lapTime()).isEqualTo(92.0);

        verify(jobLauncher).run(eq(processTelemetryJob), any(JobParameters.class));
    }

    @Test
    void onTelemetryEvent_duplicateMessage_skipsProcessing() {
        RaceTelemetryEvent event = new RaceTelemetryEvent(
                "msg-dup", 1L, 1L,
                List.of(new RaceTelemetryEvent.Reading(80.0, 105.0, 24.5, 92.0))
        );
        when(messageRepository.existsByMessageId("msg-dup")).thenReturn(true);

        listener.onTelemetryEvent(event);

        verify(telemetryService, never()).ingestBatch(any());
        verify(messageRepository, never()).save(any());
    }
}
