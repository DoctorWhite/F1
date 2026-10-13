package team2.race_service.repository;

import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import team2.race_service.api.Driver;
import team2.race_service.data.entity.Race;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.data.enums.Weather;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class FixturesPersistence {

    public static void persistSpaRace(TestEntityManager tem) {
        RaceResult spaResult = RaceResult.builder()
                .leaderboard(Map.of(
                        1, new Driver(1L, "Charles", "Leclerc"),
                        2, new Driver(2L, "Lando", "Norris")
                ))
                .fastestLapDriverId(1L)
                .build();
        tem.persist(spaResult);

        Race spa = new Race(
                null,
                "Stavelot",
                LocalDate.of(2022, 1, 1),
                Weather.CLOUDY,
                spaResult,
                new ArrayList<>()
        );
        tem.persist(spa);
        tem.flush();
    }

    public static void persistSilverstoneRace(TestEntityManager tem) {
        RaceResult silverstoneResult = RaceResult.builder()
                .leaderboard(Map.of(
                        1, new Driver(1L, "Charles", "Leclerc"),
                        2, new Driver(3L, "Max", "Verstappen")
                ))
                .fastestLapDriverId(1L)
                .build();
        tem.persist(silverstoneResult);

        Race silverstone = new Race(
                null,
                "Silverstone",
                LocalDate.of(2022, 2, 1),
                Weather.RAINY,
                silverstoneResult,
                new ArrayList<>()
        );

        tem.persist(silverstone);
        tem.flush();
    }

    public static void persistNextSilverstoneRace(TestEntityManager tem) {
        RaceResult silverstoneResult = RaceResult.builder()
                .leaderboard(Map.of(
                        1, new Driver(4L, "Šarls", "Leklérk"),
                        2, new Driver(5L, "Lujis", "Chemiltn")
                ))
                .fastestLapDriverId(5L)
                .build();
        tem.persist(silverstoneResult);

        Race silverstone = new Race(
                null,
                "Silverstone",
                LocalDate.of(2023, 2, 1),
                Weather.RAINY,
                silverstoneResult,
                new ArrayList<>()
        );

        tem.persist(silverstone);
        tem.flush();
    }

    public static void persistMonzaRace(TestEntityManager tem) {
        RaceResult monzaResult = RaceResult.builder()
                .leaderboard(Map.of(
                        1, new Driver(1L, "Charles", "Leclerc"),
                        2, new Driver(6L, "Lewis", "Hamilton")
                ))
                .fastestLapDriverId(1L)
                .build();
        tem.persist(monzaResult);

        Race monza = new Race(
                null,
                "Monza",
                LocalDate.of(2023, 1, 1),
                Weather.SUNNY,
                monzaResult,
                new ArrayList<>()
        );

        tem.persist(monza);
        tem.flush();
    }

}
