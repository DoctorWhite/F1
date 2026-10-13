package team2.driverservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import team2.driverservice.config.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
class DriverServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
