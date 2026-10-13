package team2.race_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "f1-logic.points")
public class PointsConfig {

    private Map<Integer, Integer> mapping;
    private FastestLap fastestLap;

    @Data
    public static class FastestLap {
        private int bonus;
        private int minPosition;
    }

    public int getPointsForPosition(int position) {
        return mapping.getOrDefault(position, 0);
    }
}