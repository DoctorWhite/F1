package team2.driverservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import team2.driverservice.config.DriverServiceProperties;

@SpringBootApplication
@EnableConfigurationProperties(DriverServiceProperties.class)
public class DriverServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(DriverServiceApplication.class, args);
    }

}
