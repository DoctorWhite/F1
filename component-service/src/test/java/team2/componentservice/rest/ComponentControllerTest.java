package team2.componentservice.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import team2.componentservice.config.TestSecurityConfig;
import team2.componentservice.api.ComponentViewDto;
import team2.componentservice.api.CreateComponentDto;
import team2.componentservice.api.UpdateComponentDto;
import team2.componentservice.data.enums.ComponentType;
import team2.componentservice.exception.ComponentNotFoundException;
import team2.componentservice.facade.ComponentFacade;
import team2.componentservice.utils.ObjectConverter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ComponentController.class, excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@Import(TestSecurityConfig.class)
@WithMockUser(authorities = {"SCOPE_test_read", "SCOPE_test_write"})
class ComponentControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private ComponentFacade componentFacade;

    @Test
    void findById_componentExists_returnsComponent() throws Exception {
        // Arrange
        ComponentViewDto dto = dto(1L, "V6 Turbo", ComponentType.ENGINE, "2.1", 95.0, 90.0, "Powertrain");
        when(componentFacade.findById(1L)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/components/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("V6 Turbo"));

        verify(componentFacade).findById(1L);
    }

    @Test
    void findAll_withoutFilters_usesFindAllFacadeMethod() throws Exception {
        // Arrange
        PageImpl<ComponentViewDto> page = new PageImpl<>(List.of(dto(1L, "Comp", ComponentType.ENGINE, "1.0", 90.0, 90.0, "Powertrain")));
        when(componentFacade.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/components").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(componentFacade).findAll(PageRequest.of(0, 10));
    }

    @Test
    void findAll_withType_usesFindByTypeFacadeMethod() throws Exception {
        // Arrange
        PageImpl<ComponentViewDto> page = new PageImpl<>(List.of(dto(2L, "Wing", ComponentType.AERO, "2.0", 94.0, 88.0, "Aero")));
        when(componentFacade.findByType(eq(ComponentType.AERO), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/components").param("type", "AERO").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("AERO"));

        verify(componentFacade).findByType(eq(ComponentType.AERO), eq(PageRequest.of(0, 5)));
    }

    @Test
    void findAll_withUnknownType_returnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/components")
                        .param("type", "UNKNOWN_TYPE")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());

        verify(componentFacade, org.mockito.Mockito.never())
                .findByType(any(ComponentType.class), any(Pageable.class));
    }

    @Test
    void findAll_withDepartment_usesFindByDepartmentFacadeMethod() throws Exception {
        // Arrange
        PageImpl<ComponentViewDto> page = new PageImpl<>(List.of(dto(3L, "ECU", ComponentType.ELECTRONICS, "1.1", 85.0, 96.0, "Electronics")));
        when(componentFacade.findByDepartment(eq("Electronics"), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/components").param("department", "Electronics").param("page", "1").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].department").value("Electronics"));

        verify(componentFacade).findByDepartment(eq("Electronics"), eq(PageRequest.of(1, 3)));
    }

    @Test
    void findAll_withTypeAndDepartment_usesCombinedFacadeMethod() throws Exception {
        // Arrange
        PageImpl<ComponentViewDto> page = new PageImpl<>(List.of(dto(4L, "Front Wing", ComponentType.AERO, "3.0", 97.0, 87.0, "Aero")));
        when(componentFacade.findByTypeAndDepartment(eq(ComponentType.AERO), eq("Aero"), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/components")
                        .param("type", "AERO")
                        .param("department", "Aero")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Front Wing"));

        verify(componentFacade).findByTypeAndDepartment(eq(ComponentType.AERO), eq("Aero"), eq(PageRequest.of(0, 2)));
    }

    @Test
    void getComponentTypes_noCondition_returnsEnumValues() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/components/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getBestComponentByType_validType_returnsComponent() throws Exception {
        // Arrange
        ComponentViewDto dto = dto(7L, "Best Engine", ComponentType.ENGINE, "3.2", 99.0, 98.0, "Powertrain");
        when(componentFacade.findBestComponentByType(ComponentType.ENGINE)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/components/best").param("type", "ENGINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Best Engine"));

        verify(componentFacade).findBestComponentByType(ComponentType.ENGINE);
    }

    @Test
    void createComponent_validRequest_returnsCreatedBody() throws Exception {
        // Arrange
        CreateComponentDto request = new CreateComponentDto("Sensor Pack", ComponentType.ELECTRONICS, "1.0", 84.0, 97.0, "Electronics");
        ComponentViewDto response = dto(10L, "Sensor Pack", ComponentType.ELECTRONICS, "1.0", 84.0, 97.0, "Electronics");
        when(componentFacade.createComponent(request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/components")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Sensor Pack"));

        verify(componentFacade).createComponent(request);
    }

    @Test
    void createComponent_invalidRequest_returnsBadRequest() throws Exception {
        // Arrange
        CreateComponentDto request = new CreateComponentDto("", ComponentType.ENGINE, "", null, 90.0, "");

        // Act & Assert
        mockMvc.perform(post("/api/v1/components")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void updateComponent_validRequest_returnsBody() throws Exception {
        // Arrange
        UpdateComponentDto request = new UpdateComponentDto("Updated", ComponentType.ENGINE, "2.2", 96.0, 91.0, "Powertrain");
        ComponentViewDto response = dto(5L, "Updated", ComponentType.ENGINE, "2.2", 96.0, 91.0, "Powertrain");
        when(componentFacade.updateComponent(5L, request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/v1/components/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(componentFacade).updateComponent(5L, request);
    }

    @Test
    void updateComponent_invalidRequest_returnsBadRequest() throws Exception {
        // Arrange
        UpdateComponentDto request = new UpdateComponentDto("", ComponentType.ENGINE, "", null, null, "");

        // Act & Assert
        mockMvc.perform(put("/api/v1/components/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectConverter.convertObjectToJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"));
    }

    @Test
    void deleteById_validId_returnsNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/components/{id}", 9L))
                .andExpect(status().isNoContent());

        verify(componentFacade).deleteComponentById(9L);
    }

    @Test
    void findById_componentNotFound_returnsNotFound() throws Exception {
        // Arrange
        when(componentFacade.findById(404L)).thenThrow(new ComponentNotFoundException("Component not found with id: 404"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/components/{id}", 404L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Component not found with id: 404"));
    }

    private static ComponentViewDto dto(
            Long id,
            String name,
            ComponentType type,
            String version,
            Double performanceScore,
            Double reliabilityScore,
            String department
    ) {
        return new ComponentViewDto(id, name, type, version, performanceScore, reliabilityScore, department);
    }
}
