package team2.componentservice.data.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team2.componentservice.data.entity.Component;
import team2.componentservice.data.enums.ComponentType;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends JpaRepository<Component, Long> {

    List<Component> findByType(ComponentType type);

    List<Component> findByDepartmentIgnoreCase(String department);

    List<Component> findByTypeAndDepartmentIgnoreCase(ComponentType type, String department);

    @Query("SELECT c FROM Component c WHERE c.type = :type ORDER BY (c.performanceScore * c.reliabilityScore) DESC")
    List<Component> findByTypeOrderByPerformanceReliabilityProductDesc(
            @Param("type") ComponentType type,
            Pageable pageable
    );

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Component c "
            + "WHERE LOWER(c.name) = LOWER(:name) AND c.version = :version")
    boolean existsByNameIgnoreCaseAndVersion(@Param("name") String name, @Param("version") String version);

    default Optional<Component> findBestByType(ComponentType type) {
        List<Component> ranked = findByTypeOrderByPerformanceReliabilityProductDesc(type, PageRequest.of(0, 1));
        return ranked.stream().findFirst();
    }
}
