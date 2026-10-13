package team2.componentservice.service;

import org.springframework.stereotype.Service;
import team2.componentservice.data.entity.Component;
import team2.componentservice.data.enums.ComponentType;
import team2.componentservice.data.repository.ComponentRepository;
import team2.componentservice.exception.ComponentNotFoundException;

import java.util.List;

@Service
public class ComponentServiceImpl implements ComponentService {

    private final ComponentRepository componentRepository;

    public ComponentServiceImpl(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    @Override
    public Component getComponentById(Long id) {
        return componentRepository.findById(id)
                .orElseThrow(() -> new ComponentNotFoundException("Component not found with id: " + id));
    }

    @Override
    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    @Override
    public List<Component> getComponentsByType(ComponentType type) {
        return componentRepository.findByType(type);
    }

    @Override
    public List<Component> getComponentsByDepartment(String department) {
        return componentRepository.findByDepartmentIgnoreCase(department);
    }

    @Override
    public List<Component> getComponentsByTypeAndDepartment(ComponentType type, String department) {
        return componentRepository.findByTypeAndDepartmentIgnoreCase(type, department);
    }

    @Override
    public Component createComponent(Component component) {
        return componentRepository.save(component);
    }

    @Override
    public Component updateComponent(Long id, Component component) {
        if (!componentRepository.existsById(id)) {
            throw new ComponentNotFoundException("Component not found with id: " + id);
        }
        Component toSave = new Component(
                id,
                component.getName(),
                component.getType(),
                component.getVersion(),
                component.getPerformanceScore(),
                component.getReliabilityScore(),
                component.getDepartment()
        );
        return componentRepository.save(toSave);
    }

    @Override
    public void deleteComponentById(Long id) {
        if (!componentRepository.existsById(id)) {
            throw new ComponentNotFoundException("Component not found with id: " + id);
        }
        componentRepository.deleteById(id);
    }

    @Override
    public Component getBestComponentByType(ComponentType type) {
        return componentRepository.findBestByType(type)
                .orElseThrow(() -> new ComponentNotFoundException("No component found for type: " + type));
    }
}
