package team2.componentservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.componentservice.data.entity.Component;
import team2.componentservice.data.enums.ComponentType;
import team2.componentservice.data.repository.ComponentRepository;
import team2.componentservice.exception.ComponentNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentServiceImplTest {

    @Mock
    private ComponentRepository componentRepository;

    @InjectMocks
    private ComponentServiceImpl componentService;

    @Test
    void getComponentById_componentExists_returnsEntityWhenFound() {
        // Arrange
        Component entity = sampleEntity(1L, "V6 Turbo", ComponentType.ENGINE, "2.1", 95.0, 90.0, "Powertrain");
        when(componentRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Act
        Component result = componentService.getComponentById(1L);

        // Assert
        assertSame(entity, result);
        verify(componentRepository).findById(1L);
    }

    @Test
    void getComponentById_componentMissing_throwsWhenMissing() {
        // Arrange
        when(componentRepository.findById(42L)).thenReturn(Optional.empty());

        // Act
        ComponentNotFoundException ex = assertThrows(ComponentNotFoundException.class,
                () -> componentService.getComponentById(42L));

        // Assert
        assertEquals("Component not found with id: 42", ex.getMessage());
        verify(componentRepository).findById(42L);
    }

    @Test
    void getAllComponents_componentsExist_delegatesToRepository() {
        // Arrange
        List<Component> components = List.of(
                sampleEntity(1L, "V6 Turbo", ComponentType.ENGINE, "2.1", 95.0, 90.0, "Powertrain"),
                sampleEntity(2L, "Rear Wing", ComponentType.AERO, "1.9", 92.0, 88.0, "Aero")
        );
        when(componentRepository.findAll()).thenReturn(components);

        // Act
        List<Component> result = componentService.getAllComponents();

        // Assert
        assertEquals(components, result);
        verify(componentRepository).findAll();
    }

    @Test
    void getComponentsByType_typeProvided_delegatesToRepository() {
        // Arrange
        List<Component> components = List.of(sampleEntity(1L, "V6 Turbo", ComponentType.ENGINE, "2.1", 95.0, 90.0, "Powertrain"));
        when(componentRepository.findByType(ComponentType.ENGINE)).thenReturn(components);

        // Act
        List<Component> result = componentService.getComponentsByType(ComponentType.ENGINE);

        // Assert
        assertEquals(components, result);
        verify(componentRepository).findByType(ComponentType.ENGINE);
    }

    @Test
    void getComponentsByDepartment_departmentProvided_delegatesToRepository() {
        // Arrange
        List<Component> components = List.of(sampleEntity(2L, "Rear Wing", ComponentType.AERO, "1.9", 92.0, 88.0, "Aero"));
        when(componentRepository.findByDepartmentIgnoreCase("Aero")).thenReturn(components);

        // Act
        List<Component> result = componentService.getComponentsByDepartment("Aero");

        // Assert
        assertEquals(components, result);
        verify(componentRepository).findByDepartmentIgnoreCase("Aero");
    }

    @Test
    void getComponentsByTypeAndDepartment_typeAndDepartmentProvided_delegatesToRepository() {
        // Arrange
        List<Component> components = List.of(sampleEntity(3L, "Front Wing", ComponentType.AERO, "2.0", 93.0, 89.0, "Aero"));
        when(componentRepository.findByTypeAndDepartmentIgnoreCase(ComponentType.AERO, "Aero")).thenReturn(components);

        // Act
        List<Component> result = componentService.getComponentsByTypeAndDepartment(ComponentType.AERO, "Aero");

        // Assert
        assertEquals(components, result);
        verify(componentRepository).findByTypeAndDepartmentIgnoreCase(ComponentType.AERO, "Aero");
    }

    @Test
    void createComponent_validInput_delegatesSave() {
        // Arrange
        Component entity = sampleEntity(null, "Telemetry Unit", ComponentType.ELECTRONICS, "1.0", 85.0, 96.0, "Electronics");
        Component saved = sampleEntity(10L, "Telemetry Unit", ComponentType.ELECTRONICS, "1.0", 85.0, 96.0, "Electronics");
        when(componentRepository.save(entity)).thenReturn(saved);

        // Act
        Component result = componentService.createComponent(entity);

        // Assert
        assertSame(saved, result);
        verify(componentRepository).save(entity);
    }

    @Test
    void updateComponent_componentExists_overwritesIdAndSavesWhenExists() {
        // Arrange
        Component update = sampleEntity(777L, "Updated Wing", ComponentType.AERO, "3.0", 97.0, 87.0, "Aero");
        Component expectedSaved = sampleEntity(5L, "Updated Wing", ComponentType.AERO, "3.0", 97.0, 87.0, "Aero");
        when(componentRepository.existsById(5L)).thenReturn(true);
        when(componentRepository.save(expectedSaved)).thenReturn(expectedSaved);

        // Act
        Component result = componentService.updateComponent(5L, update);

        // Assert
        assertEquals(expectedSaved, result);
        verify(componentRepository).existsById(5L);
        verify(componentRepository).save(expectedSaved);
    }

    @Test
    void updateComponent_componentMissing_throwsWhenMissing() {
        // Arrange
        Component update = sampleEntity(1L, "Updated", ComponentType.ENGINE, "2.2", 90.0, 90.0, "Powertrain");
        when(componentRepository.existsById(99L)).thenReturn(false);

        // Act
        ComponentNotFoundException ex = assertThrows(ComponentNotFoundException.class,
                () -> componentService.updateComponent(99L, update));

        // Assert
        assertEquals("Component not found with id: 99", ex.getMessage());
        verify(componentRepository).existsById(99L);
        verify(componentRepository, never()).save(update);
    }

    @Test
    void deleteById_componentExists_delegatesWhenExists() {
        // Arrange
        when(componentRepository.existsById(2L)).thenReturn(true);

        // Act
        componentService.deleteComponentById(2L);

        // Assert
        verify(componentRepository).existsById(2L);
        verify(componentRepository).deleteById(2L);
    }

    @Test
    void deleteById_componentMissing_throwsWhenMissing() {
        // Arrange
        when(componentRepository.existsById(2L)).thenReturn(false);

        // Act
        ComponentNotFoundException ex = assertThrows(ComponentNotFoundException.class,
                () -> componentService.deleteComponentById(2L));

        // Assert
        assertEquals("Component not found with id: 2", ex.getMessage());
        verify(componentRepository).existsById(2L);
        verify(componentRepository, never()).deleteById(2L);
    }

    @Test
    void getBestComponentByType_typeProvided_returnsEntityWhenPresent() {
        // Arrange
        Component best = sampleEntity(7L, "Best Engine", ComponentType.ENGINE, "3.2", 99.0, 98.0, "Powertrain");
        when(componentRepository.findBestByType(ComponentType.ENGINE)).thenReturn(Optional.of(best));

        // Act
        Component result = componentService.getBestComponentByType(ComponentType.ENGINE);

        // Assert
        assertSame(best, result);
        verify(componentRepository).findBestByType(ComponentType.ENGINE);
    }

    @Test
    void getBestComponentByType_componentMissing_throwsWhenMissing() {
        // Arrange
        when(componentRepository.findBestByType(ComponentType.SUSPENSION)).thenReturn(Optional.empty());

        // Act
        ComponentNotFoundException ex = assertThrows(ComponentNotFoundException.class,
                () -> componentService.getBestComponentByType(ComponentType.SUSPENSION));

        // Assert
        assertEquals("No component found for type: SUSPENSION", ex.getMessage());
        verify(componentRepository).findBestByType(ComponentType.SUSPENSION);
    }

    private static Component sampleEntity(
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
}
