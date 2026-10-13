package team2.race_service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import team2.race_service.data.entity.Race;
import team2.race_service.data.enums.Weather;
import team2.race_service.data.repository.RaceRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static team2.race_service.repository.FixturesPersistence.*;

@DataJpaTest
public class RaceRepositoryTest {

    @Autowired
    private RaceRepository raceRepository;

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
    public void findByLocationOrderByDateDesc_racesExist_returnsRaces() {
        List<Race> results = raceRepository.findByLocationOrderByDateDesc("Silverstone");

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getDate()).isEqualTo(LocalDate.of(2023, 2, 1)); // Descending order check
        assertThat(results.get(1).getDate()).isEqualTo(LocalDate.of(2022, 2, 1));
    }

    @Test
    public void findCompletedRacesByWeather_sunnyRaceExist_returnSunnyRace() {
        List<Race> results = raceRepository.findCompletedRacesByWeather(Weather.SUNNY);

        assertThat(results).hasSize(2);
        assertThat(results.stream().allMatch(res -> res.getWeather().equals(Weather.SUNNY))).isTrue();
        assertThat(results.getFirst().getRaceResult()).isNotNull();
    }
}
