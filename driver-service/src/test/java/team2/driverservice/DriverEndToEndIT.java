package team2.driverservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import team2.driverservice.config.TestSecurityConfig;
import team2.driverservice.dto.DriverRecommendationDto;
import team2.driverservice.dto.DriverResponseDto;
import team2.driverservice.dto.RaceConditionsRequestDto;
import team2.driverservice.model.Driver;
import team2.driverservice.model.DriverRole;
import team2.driverservice.model.WeatherCondition;
import team2.driverservice.repository.DriverRepository;
import team2.driverservice.util.ObjectConverter;
import team2.driverservice.util.TestDataFactory;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class DriverEndToEndIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverRepository driverRepository;

    @BeforeEach
    void cleanDatabase() {
        driverRepository.deleteAll();
    }

    @Test
    void createDriver_validRequest_persistsAndCanBeRetrieved() throws Exception {
        String responseJson = mockMvc.perform(post("/api/v1/drivers")
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(TestDataFactory.driverCreateRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        DriverResponseDto created = ObjectConverter.convertJsonToObject(responseJson, DriverResponseDto.class);
        assertThat(created.id()).isNotNull();
        assertThat(created.firstName()).isEqualTo("Lewis");
        assertThat(created.lastName()).isEqualTo("Hamilton");
        assertThat(driverRepository.findById(created.id())).isPresent();
    }

    @Test
    void getDriverById_driverNotFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/drivers/{id}", 999L)
                        .with(jwt().jwt(t -> t.claim("scope", "test_read"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDriver_existingDriver_persistsChangesToDatabase() throws Exception {
        String createJson = mockMvc.perform(post("/api/v1/drivers")
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(TestDataFactory.driverCreateRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        DriverResponseDto created = ObjectConverter.convertJsonToObject(createJson, DriverResponseDto.class);

        mockMvc.perform(put("/api/v1/drivers/{id}", created.id())
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(TestDataFactory.driverUpdateRequest())))
                .andExpect(status().isOk());

        Driver persisted = driverRepository.findById(created.id()).orElseThrow();
        assertThat(persisted.getWetSkill()).isEqualTo(93);
        assertThat(persisted.getAggressiveness()).isEqualTo(85);
    }

    @Test
    void deleteDriver_existingDriver_removesFromDatabase() throws Exception {
        String createJson = mockMvc.perform(post("/api/v1/drivers")
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(TestDataFactory.driverCreateRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        DriverResponseDto created = ObjectConverter.convertJsonToObject(createJson, DriverResponseDto.class);

        mockMvc.perform(delete("/api/v1/drivers/{id}", created.id())
                        .with(jwt().jwt(t -> t.claim("scope", "test_write"))))
                .andExpect(status().isNoContent());

        assertThat(driverRepository.findById(created.id())).isEmpty();
    }

    @Test
    void recommendDriver_withPersistedDrivers_returnsHighestScoringDriverForConditions() throws Exception {
        driverRepository.save(Driver.builder()
                .firstName("Low").lastName("Skill").nationality("X")
                .role(DriverRole.MAIN).wetSkill(10).aggressiveness(50).consistency(50).feedbackQuality(50)
                .build());
        driverRepository.save(Driver.builder()
                .firstName("High").lastName("Skill").nationality("Y")
                .role(DriverRole.MAIN).wetSkill(95).aggressiveness(50).consistency(50).feedbackQuality(50)
                .build());

        String responseJson = mockMvc.perform(post("/api/v1/drivers/recommendation")
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(
                                new RaceConditionsRequestDto(WeatherCondition.WET, 50, 50))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        DriverRecommendationDto recommendation = ObjectConverter.convertJsonToObject(responseJson, DriverRecommendationDto.class);
        assertThat(recommendation.recommendedDriver().firstName()).isEqualTo("High");
        assertThat(recommendation.weatherCondition()).isEqualTo(WeatherCondition.WET);
    }
}
