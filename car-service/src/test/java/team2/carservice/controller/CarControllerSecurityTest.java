package team2.carservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team2.carservice.dto.CarDTO;
import team2.carservice.entity.CarStatus;
import team2.carservice.facade.CarFacade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CarFacade carFacade;

    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void getCarsWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCarsWithReadScopeShouldReturn200() throws Exception {
        mockMvc.perform(get("/cars")
                        .with(jwt().authorities(() -> "SCOPE_test_read")))
                .andExpect(status().isOk());
    }

    @Test
    void getCarsWithWrongScopeShouldReturn403() throws Exception {
        mockMvc.perform(get("/cars")
                        .with(jwt().authorities(() -> "SCOPE_test_write")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCarWithWriteScopeShouldReturn201() throws Exception {
        when(carFacade.create(any()))
                .thenReturn(new CarDTO(1L, "McLaren MCL38", null, null, null, null, null, CarStatus.NEW, null));

        mockMvc.perform(post("/cars")
                        .with(jwt().authorities(() -> "SCOPE_test_write"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "McLaren MCL38"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void createCarWithReadScopeShouldReturn403() throws Exception {
        mockMvc.perform(post("/cars")
                        .with(jwt().authorities(() -> "SCOPE_test_read"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "McLaren MCL38"
                                }
                                """))
                .andExpect(status().isForbidden());
    }
}