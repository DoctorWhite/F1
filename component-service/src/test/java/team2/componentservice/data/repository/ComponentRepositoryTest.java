package team2.componentservice.data.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import team2.componentservice.data.entity.Component;
import team2.componentservice.data.enums.ComponentType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ComponentRepositoryTest {

    @Autowired
    private ComponentRepository repository;

    @BeforeEach
    void clearAndSeed() {
        repository.deleteAll();
        repository.save(component(null, "V6 Turbo", ComponentType.ENGINE, "2.1", 95.0, 90.0, "Powertrain"));
        repository.save(component(null, "V8", ComponentType.ENGINE, "1.0", 80.0, 80.0, "Powertrain"));
        repository.save(component(null, "Rear Wing", ComponentType.AERO, "1.9", 92.0, 88.0, "Aero"));
        repository.save(component(null, "ECU", ComponentType.ELECTRONICS, "1.2", 85.0, 96.0, "Electronics"));
    }

    @Test
    void save_newComponent_assignsIdAndPersists() {
        Component fresh = component(null, "Front Wing", ComponentType.AERO, "2.0", 93.0, 89.0, "Aero");

        Component saved = repository.save(fresh);

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).isPresent();
        assertThat(repository.findById(saved.getId()).orElseThrow().getName()).isEqualTo("Front Wing");
    }

    @Test
    void findByType_filtersByEngine() {
        List<Component> engines = repository.findByType(ComponentType.ENGINE);

        assertThat(engines).hasSize(2);
        assertThat(engines).extracting(Component::getName).containsExactlyInAnyOrder("V6 Turbo", "V8");
    }

    @Test
    void findByDepartmentIgnoreCase_matchesRegardlessOfCase() {
        List<Component> lower = repository.findByDepartmentIgnoreCase("aero");
        List<Component> upper = repository.findByDepartmentIgnoreCase("AERO");

        assertThat(lower).hasSize(1);
        assertThat(upper).hasSize(1);
        assertThat(lower.get(0).getName()).isEqualTo("Rear Wing");
    }

    @Test
    void findByTypeAndDepartmentIgnoreCase_filtersBoth() {
        List<Component> found = repository.findByTypeAndDepartmentIgnoreCase(ComponentType.AERO, "aero");

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Rear Wing");
    }

    @Test
    void findByDepartmentIgnoreCase_findsAllWhenInputDiffersInCaseFromStoredDepartment() {
        assertThat(repository.findByDepartmentIgnoreCase("powertrain")).hasSize(2);
    }

    @Test
    void findByTypeAndDepartmentIgnoreCase_matchesWhenDepartmentDiffersInCase() {
        assertThat(repository.findByTypeAndDepartmentIgnoreCase(ComponentType.ELECTRONICS, "electronics")).hasSize(1);
    }

    @Test
    void findByTypeOrderByPerformanceReliabilityProductDesc_ordersByProductDescending() {
        List<Component> top = repository.findByTypeOrderByPerformanceReliabilityProductDesc(
                ComponentType.ENGINE,
                PageRequest.of(0, 1)
        );

        assertThat(top).hasSize(1);
        // V6: 95*90=8550, V8: 80*80=6400
        assertThat(top.get(0).getName()).isEqualTo("V6 Turbo");
    }

    @Test
    void findBestByType_returnsHighestPerformanceTimesReliability() {
        Optional<Component> best = repository.findBestByType(ComponentType.ENGINE);

        assertThat(best).isPresent();
        assertThat(best.orElseThrow().getName()).isEqualTo("V6 Turbo");
    }

    @Test
    void existsByNameIgnoreCaseAndVersion_trueWhenMatch() {
        assertThat(repository.existsByNameIgnoreCaseAndVersion("v6 turbo", "2.1")).isTrue();
        assertThat(repository.existsByNameIgnoreCaseAndVersion("V6 TURBO", "2.1")).isTrue();
    }

    @Test
    void existsByNameIgnoreCaseAndVersion_falseWhenVersionDiffers() {
        assertThat(repository.existsByNameIgnoreCaseAndVersion("V6 Turbo", "9.9")).isFalse();
    }

    @Test
    void deleteById_removesRow() {
        Long id = repository.findByType(ComponentType.ELECTRONICS).get(0).getId();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    private static Component component(
            Long id,
            String name,
            ComponentType type,
            String version,
            double performanceScore,
            double reliabilityScore,
            String department
    ) {
        return new Component(id, name, type, version, performanceScore, reliabilityScore, department);
    }
}
