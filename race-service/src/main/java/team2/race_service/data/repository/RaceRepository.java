package team2.race_service.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team2.race_service.data.entity.Race;
import team2.race_service.data.enums.Weather;

import java.util.List;
import java.util.Optional;


public interface RaceRepository extends JpaRepository<Race, Long> {

    List<Race> findByLocationOrderByDateDesc(String location);

    @Query("SELECT r FROM Race r WHERE r.weather = :weather AND r.raceResult IS NOT NULL")
    List<Race> findCompletedRacesByWeather(@Param("weather") Weather weather);
}
