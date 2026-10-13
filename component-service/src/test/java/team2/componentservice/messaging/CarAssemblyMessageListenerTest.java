package team2.componentservice.messaging;

import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.componentservice.data.entity.CarAssemblyInboundMessage;
import team2.componentservice.data.repository.CarAssemblyInboundMessageRepository;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarAssemblyMessageListenerTest {

    @Mock
    private CarAssemblyInboundMessageRepository repository;

    @Mock
    private JsonMapper jsonMapper;

    private CarAssemblyMessageListener listener;

    @BeforeEach
    void setUp() {
        listener = new CarAssemblyMessageListener(repository, jsonMapper);
    }

    @Test
    void onCarAssemblyCompleted_newMessage_persistsRow() throws Exception {
        CarAssemblyCompletedEvent event = new CarAssemblyCompletedEvent(
                "mid-1",
                10L,
                Instant.parse("2026-04-21T12:00:00Z"),
                "e1",
                "s1",
                "a1",
                "g1",
                "el1"
        );
        when(repository.existsByMessageId("mid-1")).thenReturn(false);
        when(jsonMapper.writeValueAsString(event)).thenReturn("{\"messageId\":\"mid-1\"}");

        listener.onCarAssemblyCompleted(event);

        ArgumentCaptor<CarAssemblyInboundMessage> captor = ArgumentCaptor.forClass(CarAssemblyInboundMessage.class);
        verify(repository).save(captor.capture());
        CarAssemblyInboundMessage saved = captor.getValue();
        assertEquals("mid-1", saved.getMessageId());
        assertEquals(10L, saved.getCarId());
        assertEquals(Instant.parse("2026-04-21T12:00:00Z"), saved.getAssembledAt());
        assertEquals("{\"messageId\":\"mid-1\"}", saved.getPayloadJson());
    }

    @Test
    void onCarAssemblyCompleted_duplicateMessageId_skipsSave() {
        CarAssemblyCompletedEvent event = new CarAssemblyCompletedEvent(
                "dup",
                1L,
                Instant.now(),
                "e", "s", "a", "g", "el"
        );
        when(repository.existsByMessageId("dup")).thenReturn(true);

        listener.onCarAssemblyCompleted(event);

        verify(repository, never()).save(any());
    }

    @Test
    void onCarAssemblyCompleted_blankMessageId_throws() {
        CarAssemblyCompletedEvent event = new CarAssemblyCompletedEvent(
                "  ",
                1L,
                Instant.now(),
                "e", "s", "a", "g", "el"
        );

        assertThrows(IllegalArgumentException.class, () -> listener.onCarAssemblyCompleted(event));
        verify(repository, never()).save(any());
    }

    @Test
    void onCarAssemblyCompleted_serializationFails_propagates() throws Exception {
        CarAssemblyCompletedEvent event = new CarAssemblyCompletedEvent(
                "x",
                1L,
                Instant.now(),
                "e", "s", "a", "g", "el"
        );
        when(repository.existsByMessageId("x")).thenReturn(false);
        when(jsonMapper.writeValueAsString(event)).thenThrow(new RuntimeException("fail"));

        assertThrows(IllegalStateException.class, () -> listener.onCarAssemblyCompleted(event));
    }
}
