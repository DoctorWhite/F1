package team2.componentservice.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import team2.componentservice.api.ComponentViewDto;
import team2.componentservice.api.CreateComponentDto;
import team2.componentservice.api.UpdateComponentDto;
import team2.componentservice.data.entity.Component;
import team2.componentservice.data.enums.ComponentType;
import team2.componentservice.mapper.ComponentMapper;
import team2.componentservice.service.ComponentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentFacadeImpl implements ComponentFacade {

    private final ComponentService componentService;
    private final ComponentMapper componentMapper;

    @Override
    public ComponentViewDto findById(Long id) {
        return componentMapper.toDto(componentService.getComponentById(id));
    }

    @Override
    public Page<ComponentViewDto> findAll(Pageable pageable) {
        return toPage(componentService.getAllComponents(), pageable);
    }

    @Override
    public Page<ComponentViewDto> findByType(ComponentType type, Pageable pageable) {
        return toPage(componentService.getComponentsByType(type), pageable);
    }

    @Override
    public Page<ComponentViewDto> findByDepartment(String department, Pageable pageable) {
        return toPage(componentService.getComponentsByDepartment(department), pageable);
    }

    @Override
    public Page<ComponentViewDto> findByTypeAndDepartment(ComponentType type, String department, Pageable pageable) {
        return toPage(componentService.getComponentsByTypeAndDepartment(type, department), pageable);
    }

    private Page<ComponentViewDto> toPage(List<Component> entities, Pageable pageable) {
        List<ComponentViewDto> content = entities.stream()
                .map(componentMapper::toDto)
                .toList();
        int total = content.size();
        int start = (int) Math.min(pageable.getOffset(), total);
        int end = Math.min(start + pageable.getPageSize(), total);
        if (start >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }
        return new PageImpl<>(content.subList(start, end), pageable, total);
    }

    @Override
    public ComponentViewDto createComponent(CreateComponentDto createComponentDto) {
        return componentMapper.toDto(componentService.createComponent(componentMapper.toEntity(createComponentDto)));
    }

    @Override
    public ComponentViewDto updateComponent(Long id, UpdateComponentDto updateComponentDto) {
        return componentMapper.toDto(
                componentService.updateComponent(id, componentMapper.toEntity(id, updateComponentDto)));
    }

    @Override
    public void deleteComponentById(Long id) {
        componentService.deleteComponentById(id);
    }

    @Override
    public ComponentViewDto findBestComponentByType(ComponentType type) {
        return componentMapper.toDto(componentService.getBestComponentByType(type));
    }
}
