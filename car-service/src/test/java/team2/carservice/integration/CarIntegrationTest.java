package team2.carservice.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CarIntegrationTest {

    @MockitoBean
    JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAssembleAndGetCar_endToEnd_worksWithDatabase() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String createRequest = """
                {
                  "name": "Ferrari F1-24"
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/cars")
                        .with(jwt().authorities(() -> "SCOPE_test_write"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ferrari F1-24"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andReturn();

        String createResponseBody = createResult.getResponse().getContentAsString();
        JsonNode createdCar = objectMapper.readTree(createResponseBody);
        long carId = createdCar.get("id").asLong();

        String assembleRequest = """
                {
                  "carId": %d,
                  "engineId": "engine-1",
                  "suspensionId": "suspension-1",
                  "aerodynamicsId": "aero-1",
                  "gearboxId": "gearbox-1",
                  "electronicsId": "electronics-1"
                }
                """.formatted(carId);

        mockMvc.perform(post("/cars/assemble")
                        .with(jwt().authorities(() -> "SCOPE_test_write"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assembleRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carId))
                .andExpect(jsonPath("$.name").value("Ferrari F1-24"))
                .andExpect(jsonPath("$.status").value("ASSEMBLED"))
                .andExpect(jsonPath("$.engineId").value("engine-1"));

        mockMvc.perform(get("/cars/" + carId)
                        .with(jwt().authorities(() -> "SCOPE_test_read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carId))
                .andExpect(jsonPath("$.name").value("Ferrari F1-24"))
                .andExpect(jsonPath("$.status").value("ASSEMBLED"))
                .andExpect(jsonPath("$.engineId").value("engine-1"))
                .andExpect(jsonPath("$.suspensionId").value("suspension-1"))
                .andExpect(jsonPath("$.aerodynamicsId").value("aero-1"))
                .andExpect(jsonPath("$.gearboxId").value("gearbox-1"))
                .andExpect(jsonPath("$.electronicsId").value("electronics-1"));
    }
}