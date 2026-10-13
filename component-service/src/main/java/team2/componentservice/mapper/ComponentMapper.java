package team2.componentservice.mapper;

import team2.componentservice.api.ComponentViewDto;
import team2.componentservice.api.CreateComponentDto;
import team2.componentservice.api.UpdateComponentDto;
import team2.componentservice.data.entity.Component;

@org.springframework.stereotype.Component
public class ComponentMapper {

    public Component toEntity(CreateComponentDto dto) {
        return new Component(
                null,
                dto.name(),
                dto.type(),
                dto.version(),
                dto.performanceScore(),
                dto.reliabilityScore(),
                dto.department()
        );
    }

    /**
     * @param id component id from the path (authoritative)
     */
    public Component toEntity(Long id, UpdateComponentDto dto) {
        return new Component(
                id,
                dto.name(),
                dto.type(),
                dto.version(),
                dto.performanceScore(),
                dto.reliabilityScore(),
                dto.department()
        );
    }

    public ComponentViewDto toDto(Component entity) {
        return new ComponentViewDto(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getVersion(),
                entity.getPerformanceScore(),
                entity.getReliabilityScore(),
                entity.getDepartment()
        );
    }
}
