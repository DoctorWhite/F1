package team2.componentservice.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team2.componentservice.api.CreateComponentDto;
import team2.componentservice.config.TestSecurityConfig;
import team2.componentservice.data.enums.ComponentType;
import team2.componentservice.facade.ComponentFacade;
import team2.componentservice.util.ComponentTestDataFactory;
import team2.componentservice.utils.ObjectConverter;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class ComponentSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComponentFacade componentFacade;

    @Test
    void getComponent_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/components/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getComponent_withReadScope_returnsOk() throws Exception {
        when(componentFacade.findById(1L)).thenReturn(ComponentTestDataFactory.COMPONENT_VIEW);

        mockMvc.perform(get("/api/v1/components/{id}", 1L)
                        .with(jwt().jwt(token -> token.claim("scope", "test_read"))))
                .andExpect(status().isOk());
    }

    @Test
    void createComponent_withReadScope_returnsForbidden() throws Exception {
        CreateComponentDto request = new CreateComponentDto(
                "Qualifying Gearbox",
                ComponentType.GEARBOX,
                "qualy-2026",
                96.0,
                90.0,
                "Transmission"
        );

        mockMvc.perform(post("/api/v1/components")
                        .with(jwt().jwt(token -> token.claim("scope", "test_read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isForbidden());
    }
}
