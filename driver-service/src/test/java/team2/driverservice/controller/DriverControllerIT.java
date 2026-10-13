package team2.driverservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team2.driverservice.config.TestSecurityConfig;
import team2.driverservice.dto.DriverCreateRequestDto;
import team2.driverservice.dto.DriverLineupSuggestionDto;
import team2.driverservice.dto.DriverRankingEntryDto;
import team2.driverservice.dto.DriverRecommendationDto;
import team2.driverservice.dto.DriverResponseDto;
import team2.driverservice.dto.DriverUpdateRequestDto;
import team2.driverservice.facade.DriverFacade;
import team2.driverservice.util.ObjectConverter;
import team2.driverservice.util.TestDataFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class DriverControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverFacade driverFacade;

    @Test
    void createDriver_validRequest_returnsCreatedDriver() throws Exception {
        DriverCreateRequestDto request = TestDataFactory.driverCreateRequest();
        when(driverFacade.createDriver(any(DriverCreateRequestDto.class)))
                .thenReturn(TestDataFactory.DRIVER_RESPONSE_LEWIS);

        String responseJson = mockMvc.perform(post("/api/v1/drivers")
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        DriverResponseDto response = ObjectConverter.convertJsonToObject(responseJson, DriverResponseDto.class);
        assertThat(response).isEqualTo(TestDataFactory.DRIVER_RESPONSE_LEWIS);
        verify(driverFacade).createDriver(any(DriverCreateRequestDto.class));
    }

    @Test
    void getAllDrivers_driversExist_returnsList() throws Exception {
        List<DriverResponseDto> list = List.of(
                TestDataFactory.DRIVER_RESPONSE_LEWIS,
                TestDataFactory.DRIVER_RESPONSE_CHARLES);
        when(driverFacade.getAllDrivers()).thenReturn(list);

        String responseJson = mockMvc.perform(get("/api/v1/drivers")
                        .with(jwt().jwt(t -> t.claim("scope", "test_read")))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        DriverResponseDto[] response = ObjectConverter.convertJsonToObject(responseJson, DriverResponseDto[].class);
        assertThat(response.length).isEqualTo(2);
        verify(driverFacade).getAllDrivers();
    }

    @Test
    void getDriverById_driverFound_returnsDriver() throws Exception {
        when(driverFacade.getDriverById(1L)).thenReturn(TestDataFactory.DRIVER_RESPONSE_LEWIS);

        String responseJson = mockMvc.perform(get("/api/v1/drivers/{id}", 1L)
                        .with(jwt().jwt(t -> t.claim("scope", "test_read")))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        DriverResponseDto response = ObjectConverter.convertJsonToObject(responseJson, DriverResponseDto.class);
        assertThat(response).isEqualTo(TestDataFactory.DRIVER_RESPONSE_LEWIS);
        verify(driverFacade).getDriverById(1L);
    }

    @Test
    void recommendDriver_wetConditions_returnsRecommendation() throws Exception {
        DriverRecommendationDto dto = TestDataFactory.driverRecommendationDto();
        when(driverFacade.recommendDriver(any())).thenReturn(dto);

        String responseJson = mockMvc.perform(post("/api/v1/drivers/recommendation")
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(TestDataFactory.wetRaceConditions())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        DriverRecommendationDto response = ObjectConverter.convertJsonToObject(responseJson, DriverRecommendationDto.class);
        assertThat(response).isEqualTo(dto);
        verify(driverFacade).recommendDriver(any());
    }

    @Test
    void updateDriver_validRequest_returnsDriver() throws Exception {
        DriverUpdateRequestDto request = TestDataFactory.driverUpdateRequest();
        when(driverFacade.updateDriver(eq(1L), any(DriverUpdateRequestDto.class)))
                .thenReturn(TestDataFactory.DRIVER_RESPONSE_LEWIS);

        String responseJson = mockMvc.perform(put("/api/v1/drivers/{id}", 1L)
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        DriverResponseDto response = ObjectConverter.convertJsonToObject(responseJson, DriverResponseDto.class);
        assertThat(response).isEqualTo(TestDataFactory.DRIVER_RESPONSE_LEWIS);
        verify(driverFacade).updateDriver(eq(1L), any(DriverUpdateRequestDto.class));
    }

    @Test
    void deleteDriver_existingId_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/drivers/{id}", 1L)
                        .with(jwt().jwt(t -> t.claim("scope", "test_write"))))
                .andExpect(status().isNoContent());

        verify(driverFacade).deleteDriver(1L);
    }

    @Test
    void rankDrivers_conditionsProvided_returnsRankings() throws Exception {
        List<DriverRankingEntryDto> rankings = List.of(TestDataFactory.driverRankingEntryFirst());
        when(driverFacade.rankDrivers(any())).thenReturn(rankings);

        String responseJson = mockMvc.perform(post("/api/v1/drivers/rankings")
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(TestDataFactory.wetRaceConditions())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        DriverRankingEntryDto[] response = ObjectConverter.convertJsonToObject(responseJson, DriverRankingEntryDto[].class);
        assertThat(response.length).isEqualTo(1);
        verify(driverFacade).rankDrivers(any());
    }

    @Test
    void suggestRaceLineup_conditionsProvided_returnsLineup() throws Exception {
        DriverLineupSuggestionDto dto = TestDataFactory.driverLineupSuggestionDto();
        when(driverFacade.suggestRaceLineup(any())).thenReturn(dto);

        String responseJson = mockMvc.perform(post("/api/v1/drivers/lineup-suggestions")
                        .with(jwt().jwt(t -> t.claim("scope", "test_write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(TestDataFactory.wetRaceConditions())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        DriverLineupSuggestionDto response = ObjectConverter.convertJsonToObject(responseJson, DriverLineupSuggestionDto.class);
        assertThat(response).isEqualTo(dto);
        verify(driverFacade).suggestRaceLineup(any());
    }
}
