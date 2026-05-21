package korobkin.nikita.project_service.repository;

import korobkin.nikita.project_service.entity.ProjectView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectViewRepository extends JpaRepository<ProjectView, UUID> {
}
