package team2.race_service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.data.repository.RaceResultRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static team2.race_service.repository.FixturesPersistence.*;

@DataJpaTest
public class RaceResultRepositoryTest {

    @Autowired
    private RaceResultRepository raceResultRepository;

    @Autowired
    private TestEntityManager tem;

    @BeforeEach
    public void setup() {
        persistSpaRace(tem);
        persistSilverstoneRace(tem);
        persistNextSilverstoneRace(tem);
        persistMonzaRace(tem);
    }

    @Test
    public void shouldReturnRacesByLocationOrderedByDateDesc() {
        List<RaceResult> results = raceResultRepository.findByFastestLapDriverId(1L);

        assertThat(results).hasSize(4);
        assertThat(results.get(0).getFastestLapDriverId()).isEqualTo(1L); // Descending order check
        assertThat(results.get(1).getFastestLapDriverId()).isEqualTo(1L);
        assertThat(results.get(2).getFastestLapDriverId()).isEqualTo(1L);
        assertThat(results.get(3).getFastestLapDriverId()).isEqualTo(1L);
    }
}
