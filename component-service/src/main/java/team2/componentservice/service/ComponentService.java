package team2.componentservice.service;

import team2.componentservice.data.entity.Component;
import team2.componentservice.data.enums.ComponentType;

import java.util.List;

public interface ComponentService {
    Component getComponentById(Long id);

    List<Component> getAllComponents();

    List<Component> getComponentsByType(ComponentType type);

    List<Component> getComponentsByDepartment(String department);

    List<Component> getComponentsByTypeAndDepartment(ComponentType type, String department);

    Component createComponent(Component component);

    Component updateComponent(Long id, Component component);

    void deleteComponentById(Long id);

    Component getBestComponentByType(ComponentType type);
}
