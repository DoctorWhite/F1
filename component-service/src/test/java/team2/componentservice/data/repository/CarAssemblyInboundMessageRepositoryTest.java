package team2.componentservice.data.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import team2.componentservice.data.entity.CarAssemblyInboundMessage;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CarAssemblyInboundMessageRepositoryTest {

    @Autowired
    private CarAssemblyInboundMessageRepository repository;

    @BeforeEach
    void clear() {
        repository.deleteAll();
    }

    @Test
    void save_and_existsByMessageId() {
        assertThat(repository.existsByMessageId("m-1")).isFalse();

        repository.save(new CarAssemblyInboundMessage(
                null,
                "m-1",
                99L,
                Instant.parse("2026-01-01T00:00:00Z"),
                "{}",
                Instant.now()
        ));

        assertThat(repository.existsByMessageId("m-1")).isTrue();
        assertThat(repository.findAll()).hasSize(1);
    }
}
