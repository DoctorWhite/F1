package team2.componentservice.facade;

import team2.componentservice.data.enums.ComponentType;
import team2.componentservice.api.CreateComponentDto;
import team2.componentservice.api.UpdateComponentDto;
import team2.componentservice.api.ComponentViewDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComponentFacade {

    ComponentViewDto findById(Long id);
    Page<ComponentViewDto> findAll(Pageable pageable);
    Page<ComponentViewDto> findByType(ComponentType type, Pageable pageable);
    Page<ComponentViewDto> findByDepartment(String department, Pageable pageable);
    Page<ComponentViewDto> findByTypeAndDepartment(ComponentType type, String department, Pageable pageable);
    ComponentViewDto createComponent(CreateComponentDto createComponentDto);
    ComponentViewDto updateComponent(Long id, UpdateComponentDto updateComponentDto);
    void deleteComponentById(Long id);
    ComponentViewDto findBestComponentByType(ComponentType type);
}
