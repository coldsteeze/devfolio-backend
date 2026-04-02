package korobkin.nikita.project_service.repository;

import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectSkillRepository extends JpaRepository<ProjectSkill, UUID> {

    Optional<ProjectSkill> findProjectSkillByProjectAndSkillId(Project project, UUID skillId);

    Boolean existsByProjectAndSkillId(Project project, UUID skillId);

    List<ProjectSkill> findByProjectId(UUID projectId);
}
