package team2.componentservice.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.componentservice.api.ComponentViewDto;
import team2.componentservice.api.CreateComponentDto;
import team2.componentservice.api.UpdateComponentDto;
import team2.componentservice.data.enums.ComponentType;
import team2.componentservice.facade.ComponentFacade;

@RestController
@RequestMapping(path = "${api.base-path:/api/v1}/components")
@RequiredArgsConstructor
@Tag(name = "Component Management", description = "Endpoints for managing F1 car components. Base path: /api/v1")
public class ComponentController {

    private final ComponentFacade componentFacade;

    @Operation(summary = "Get component details by ID")
    @GetMapping(path = "/{id}")
    public ResponseEntity<ComponentViewDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(componentFacade.findById(id));
    }

    @Operation(summary = "List components (paged)", description = "Optional filters: type, department, or both. Uses Spring Data pageable query params (page, size, sort).")
    @GetMapping
    public ResponseEntity<Page<ComponentViewDto>> findAll(
            @RequestParam(required = false) ComponentType type,
            @RequestParam(required = false) String department,
            Pageable pageable) {
        if (type != null && department != null) {
            return ResponseEntity.ok(componentFacade.findByTypeAndDepartment(type, department, pageable));
        }
        if (type != null) {
            return ResponseEntity.ok(componentFacade.findByType(type, pageable));
        }
        if (department != null) {
            return ResponseEntity.ok(componentFacade.findByDepartment(department, pageable));
        }
        return ResponseEntity.ok(componentFacade.findAll(pageable));
    }

    @Operation(summary = "Get available component types", description = "Returns all component types. Useful for other services to discover what types to request.")
    @GetMapping("/types")
    public ResponseEntity<ComponentType[]> getComponentTypes() {
        return ResponseEntity.ok(ComponentType.values());
    }

    @Operation(summary = "Get the best component by type", description = "Returns the component with highest performanceScore * reliabilityScore for the given type.")
    @GetMapping("/best")
    public ResponseEntity<ComponentViewDto> getBestComponentByType(@RequestParam ComponentType type) {
        return ResponseEntity.ok(componentFacade.findBestComponentByType(type));
    }

    @Operation(summary = "Create a new component", description = "Server assigns the component ID.")
    @PostMapping
    public ResponseEntity<ComponentViewDto> createComponent(@Valid @RequestBody CreateComponentDto createComponentDto) {
        return ResponseEntity.ok(componentFacade.createComponent(createComponentDto));
    }

    @Operation(summary = "Update a component", description = "ID is taken from the path, not the body.")
    @PutMapping("/{id}")
    public ResponseEntity<ComponentViewDto> updateComponent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateComponentDto updateComponentDto) {
        return ResponseEntity.ok(componentFacade.updateComponent(id, updateComponentDto));
    }

    @Operation(summary = "Delete a component by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComponentById(@PathVariable Long id) {
        componentFacade.deleteComponentById(id);
        return ResponseEntity.noContent().build();
    }
}
