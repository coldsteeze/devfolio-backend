package korobkin.nikita.skill_service.repository;

import korobkin.nikita.skill_service.dto.request.SkillFilterRequest;
import korobkin.nikita.skill_service.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {

    @Query("""
            SELECT s FROM Skill s
            WHERE (:#{#f.search} IS NULL\s
                   OR LOWER(s.name) LIKE LOWER(CONCAT('%', :#{#f.search}, '%')))
            AND (:#{#f.category} IS NULL\s
                   OR s.category = :#{#f.category})
            AND (:#{#f.includeInactive} = true\s
                   OR s.active = true)
           \s""")
    Page<Skill> findAllByFilters(
            @Param("f") SkillFilterRequest f,
            Pageable pageable
    );

    List<Skill> findAllByIdInAndActiveTrue(List<UUID> skillIds);

    Boolean existsByName(String name);
}
