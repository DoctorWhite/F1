package team2.driverservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverRole;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @BeforeEach
    void setUp() {
        driverRepository.deleteAll();
        driverRepository.save(Driver.builder()
                .firstName("Lewis").lastName("Hamilton").nationality("United Kingdom")
                .role(DriverRole.MAIN).wetSkill(92).aggressiveness(84).consistency(96).feedbackQuality(95)
                .build());
        driverRepository.save(Driver.builder()
                .firstName("Charles").lastName("Leclerc").nationality("Monaco")
                .role(DriverRole.MAIN).wetSkill(88).aggressiveness(90).consistency(91).feedbackQuality(89)
                .build());
        driverRepository.save(Driver.builder()
                .firstName("Nyck").lastName("De Vries").nationality("Netherlands")
                .role(DriverRole.RESERVE).wetSkill(70).aggressiveness(60).consistency(75).feedbackQuality(80)
                .build());
    }

    @Test
    void findByRole_mainRole_returnsOnlyMainDrivers() {
        List<Driver> result = driverRepository.findByRole(DriverRole.MAIN);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Driver::getLastName)
                .containsExactlyInAnyOrder("Hamilton", "Leclerc");
    }

    @Test
    void findByRole_reserveRole_returnsSingleReserveDriver() {
        List<Driver> result = driverRepository.findByRole(DriverRole.RESERVE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getLastName()).isEqualTo("De Vries");
    }

    @Test
    void findByRole_testRole_returnsEmptyList() {
        List<Driver> result = driverRepository.findByRole(DriverRole.TEST);

        assertThat(result).isEmpty();
    }

    @Test
    void findByNationality_exactMatch_returnsMatchingDriver() {
        List<Driver> result = driverRepository.findByNationality("Monaco");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFirstName()).isEqualTo("Charles");
    }

    @Test
    void findByNationality_multipleDriversSameNationality_returnsAll() {
        driverRepository.save(Driver.builder()
                .firstName("George").lastName("Russell").nationality("United Kingdom")
                .role(DriverRole.MAIN).wetSkill(85).aggressiveness(75).consistency(88).feedbackQuality(90)
                .build());

        List<Driver> result = driverRepository.findByNationality("United Kingdom");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Driver::getLastName)
                .containsExactlyInAnyOrder("Hamilton", "Russell");
    }

    @Test
    void findByNationality_noMatch_returnsEmptyList() {
        List<Driver> result = driverRepository.findByNationality("Brazil");

        assertThat(result).isEmpty();
    }
}
