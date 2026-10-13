package team2.componentservice.rest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import team2.componentservice.config.TestSecurityConfig;
import org.springframework.test.web.servlet.MockMvc;
import team2.componentservice.api.ComponentViewDto;
import team2.componentservice.exception.ComponentNotFoundException;
import team2.componentservice.facade.ComponentFacade;
import team2.componentservice.util.ComponentTestDataFactory;
import team2.componentservice.utils.ObjectConverter;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class ComponentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComponentFacade componentFacade;

    @Test
    void findById_componentFound_returnsComponent() throws Exception {
        // Arrange
        Long id = 1L;
        Mockito.when(componentFacade.findById(id)).thenReturn(ComponentTestDataFactory.COMPONENT_VIEW);

        // Act
        String responseJson = mockMvc.perform(get("/api/v1/components/{id}", id)
                        .with(jwt().jwt(token -> token.claim("scope", "test_read")))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ComponentViewDto response = ObjectConverter.convertJsonToObject(responseJson, ComponentViewDto.class);

        // Assert
        assertThat(response).isEqualTo(ComponentTestDataFactory.COMPONENT_VIEW);
    }

    @Test
    void findById_componentNotFound_returnsNotFound() throws Exception {
        // Arrange
        Long id = 404L;
        Mockito.when(componentFacade.findById(id))
                .thenThrow(new ComponentNotFoundException("Component not found with id: " + id));

        // Act & Assert
        mockMvc.perform(get("/api/v1/components/{id}", id)
                        .with(jwt().jwt(token -> token.claim("scope", "test_read")))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }
}
