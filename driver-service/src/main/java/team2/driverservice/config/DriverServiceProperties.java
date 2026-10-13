package team2.driverservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "driver-service")
public class DriverServiceProperties {
    private double wetWeatherWeight;
    private double dryWeatherWeight;
    private double mixedWeatherWeight;
    private double consistencyWeight;
    private double aggressivenessWeight;
    private double feedbackWeight;
    private double technicalDemandFeedbackBonus;
}
