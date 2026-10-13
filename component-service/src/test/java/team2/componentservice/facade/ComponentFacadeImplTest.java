package team2.componentservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import team2.componentservice.api.ComponentViewDto;
import team2.componentservice.api.CreateComponentDto;
import team2.componentservice.api.UpdateComponentDto;
import team2.componentservice.data.entity.Component;
import team2.componentservice.data.enums.ComponentType;
import team2.componentservice.mapper.ComponentMapper;
import team2.componentservice.service.ComponentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentFacadeImplTest {

    @Mock
    private ComponentService componentService;

    @Mock
    private ComponentMapper componentMapper;

    @InjectMocks
    private ComponentFacadeImpl componentFacade;

    @Test
    void findById_componentExists_returnsMappedComponent() {
        // Arrange
        Component entity = entity(1L, "V6 Turbo", ComponentType.ENGINE, "2.1", 95.0, 90.0, "Powertrain");
        ComponentViewDto dto = dto(1L, "V6 Turbo", ComponentType.ENGINE, "2.1", 95.0, 90.0, "Powertrain");
        when(componentService.getComponentById(1L)).thenReturn(entity);
        when(componentMapper.toDto(entity)).thenReturn(dto);

        // Act
        ComponentViewDto result = componentFacade.findById(1L);

        // Assert
        assertSame(dto, result);
        verify(componentService).getComponentById(1L);
        verify(componentMapper).toDto(entity);
    }

    @Test
    void findAll_componentsExist_returnsPagedMappedComponents() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        Component first = entity(1L, "Engine A", ComponentType.ENGINE, "1.0", 90.0, 90.0, "Powertrain");
        Component second = entity(2L, "Engine B", ComponentType.ENGINE, "1.1", 91.0, 89.0, "Powertrain");
        ComponentViewDto firstDto = dto(1L, "Engine A", ComponentType.ENGINE, "1.0", 90.0, 90.0, "Powertrain");
        ComponentViewDto secondDto = dto(2L, "Engine B", ComponentType.ENGINE, "1.1", 91.0, 89.0, "Powertrain");

        when(componentService.getAllComponents()).thenReturn(List.of(first, second));
        when(componentMapper.toDto(first)).thenReturn(firstDto);
        when(componentMapper.toDto(second)).thenReturn(secondDto);

        // Act
        Page<ComponentViewDto> page = componentFacade.findAll(pageable);

        // Assert
        assertEquals(2, page.getTotalElements());
        assertEquals(List.of(firstDto, secondDto), page.getContent());
        verify(componentService).getAllComponents();
    }

    @Test
    void findAll_offsetBeyondTotal_returnsEmptyPageContent() {
        // Arrange
        Pageable pageable = PageRequest.of(2, 2);
        Component only = entity(1L, "Only", ComponentType.ENGINE, "1.0", 90.0, 90.0, "Powertrain");
        ComponentViewDto onlyDto = dto(1L, "Only", ComponentType.ENGINE, "1.0", 90.0, 90.0, "Powertrain");

        when(componentService.getAllComponents()).thenReturn(List.of(only));
        when(componentMapper.toDto(only)).thenReturn(onlyDto);

        // Act
        Page<ComponentViewDto> page = componentFacade.findAll(pageable);

        // Assert
        assertEquals(1, page.getTotalElements());
        assertEquals(List.of(), page.getContent());
    }

    @Test
    void findByType_typeProvided_returnsComponentsForType() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);
        Component entity = entity(3L, "Wing", ComponentType.AERO, "2.0", 93.0, 88.0, "Aero");
        ComponentViewDto dto = dto(3L, "Wing", ComponentType.AERO, "2.0", 93.0, 88.0, "Aero");

        when(componentService.getComponentsByType(ComponentType.AERO)).thenReturn(List.of(entity));
        when(componentMapper.toDto(entity)).thenReturn(dto);

        // Act
        Page<ComponentViewDto> page = componentFacade.findByType(ComponentType.AERO, pageable);

        // Assert
        assertEquals(List.of(dto), page.getContent());
        verify(componentService).getComponentsByType(ComponentType.AERO);
    }

    @Test
    void findByDepartment_departmentProvided_returnsComponentsForDepartment() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);
        Component entity = entity(4L, "ECU", ComponentType.ELECTRONICS, "1.2", 85.0, 96.0, "Electronics");
        ComponentViewDto dto = dto(4L, "ECU", ComponentType.ELECTRONICS, "1.2", 85.0, 96.0, "Electronics");

        when(componentService.getComponentsByDepartment("Electronics")).thenReturn(List.of(entity));
        when(componentMapper.toDto(entity)).thenReturn(dto);

        // Act
        Page<ComponentViewDto> page = componentFacade.findByDepartment("Electronics", pageable);

        // Assert
        assertEquals(List.of(dto), page.getContent());
        verify(componentService).getComponentsByDepartment("Electronics");
    }

    @Test
    void findByTypeAndDepartment_typeAndDepartmentProvided_returnsFilteredComponents() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);
        Component entity = entity(5L, "Front Wing", ComponentType.AERO, "3.0", 97.0, 87.0, "Aero");
        ComponentViewDto dto = dto(5L, "Front Wing", ComponentType.AERO, "3.0", 97.0, 87.0, "Aero");

        when(componentService.getComponentsByTypeAndDepartment(ComponentType.AERO, "Aero"))
                .thenReturn(List.of(entity));
        when(componentMapper.toDto(entity)).thenReturn(dto);

        // Act
        Page<ComponentViewDto> page = componentFacade.findByTypeAndDepartment(ComponentType.AERO, "Aero", pageable);

        // Assert
        assertEquals(List.of(dto), page.getContent());
        verify(componentService).getComponentsByTypeAndDepartment(ComponentType.AERO, "Aero");
    }

    @Test
    void createComponent_validInput_returnsCreatedMappedComponent() {
        // Arrange
        CreateComponentDto create = new CreateComponentDto("Sensor Pack", ComponentType.ELECTRONICS, "1.0", 84.0, 97.0, "Electronics");
        Component mappedEntity = entity(null, "Sensor Pack", ComponentType.ELECTRONICS, "1.0", 84.0, 97.0, "Electronics");
        Component savedEntity = entity(10L, "Sensor Pack", ComponentType.ELECTRONICS, "1.0", 84.0, 97.0, "Electronics");
        ComponentViewDto resultDto = dto(10L, "Sensor Pack", ComponentType.ELECTRONICS, "1.0", 84.0, 97.0, "Electronics");

        when(componentMapper.toEntity(create)).thenReturn(mappedEntity);
        when(componentService.createComponent(mappedEntity)).thenReturn(savedEntity);
        when(componentMapper.toDto(savedEntity)).thenReturn(resultDto);

        // Act
        ComponentViewDto result = componentFacade.createComponent(create);

        // Assert
        assertSame(resultDto, result);
        verify(componentMapper).toEntity(create);
        verify(componentService).createComponent(mappedEntity);
        verify(componentMapper).toDto(savedEntity);
    }

    @Test
    void updateComponent_validInputWithPathId_returnsUpdatedMappedComponent() {
        // Arrange
        UpdateComponentDto update = new UpdateComponentDto("Updated", ComponentType.ENGINE, "2.2", 96.0, 91.0, "Powertrain");
        Component mapped = entity(5L, "Updated", ComponentType.ENGINE, "2.2", 96.0, 91.0, "Powertrain");
        Component updated = entity(5L, "Updated", ComponentType.ENGINE, "2.2", 96.0, 91.0, "Powertrain");
        ComponentViewDto resultDto = dto(5L, "Updated", ComponentType.ENGINE, "2.2", 96.0, 91.0, "Powertrain");

        when(componentMapper.toEntity(5L, update)).thenReturn(mapped);
        when(componentService.updateComponent(5L, mapped)).thenReturn(updated);
        when(componentMapper.toDto(updated)).thenReturn(resultDto);

        // Act
        ComponentViewDto result = componentFacade.updateComponent(5L, update);

        // Assert
        assertSame(resultDto, result);
        verify(componentMapper).toEntity(5L, update);
        verify(componentService).updateComponent(5L, mapped);
        verify(componentMapper).toDto(updated);
    }

    @Test
    void deleteComponentById_validId_callsServiceDelete() {
        // Arrange
        Long id = 11L;

        // Act
        componentFacade.deleteComponentById(id);

        // Assert
        verify(componentService).deleteComponentById(id);
    }

    @Test
    void findBestComponentByType_typeProvided_returnsMappedBestComponent() {
        // Arrange
        Component best = entity(20L, "Best Aero", ComponentType.AERO, "4.0", 99.0, 94.0, "Aero");
        ComponentViewDto bestDto = dto(20L, "Best Aero", ComponentType.AERO, "4.0", 99.0, 94.0, "Aero");

        when(componentService.getBestComponentByType(ComponentType.AERO)).thenReturn(best);
        when(componentMapper.toDto(best)).thenReturn(bestDto);

        // Act
        ComponentViewDto result = componentFacade.findBestComponentByType(ComponentType.AERO);

        // Assert
        assertSame(bestDto, result);
        verify(componentService).getBestComponentByType(ComponentType.AERO);
        verify(componentMapper).toDto(best);
    }

    private static Component entity(
            Long id,
            String name,
            ComponentType type,
            String version,
            Double performanceScore,
            Double reliabilityScore,
            String department
    ) {
        return new Component(id, name, type, version, performanceScore, reliabilityScore, department);
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

