package team2.race_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import team2.race_service.config.PointsConfig;

@SpringBootApplication
@EnableConfigurationProperties(PointsConfig.class)
public class RaceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RaceServiceApplication.class, args);
	}

}
