package korobkin.nikita.project_service.repository;

import korobkin.nikita.project_service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findProjectsByUserId(UUID userId);

    List<Project> findProjectsByUserIdAndProjectPublic(UUID userId, boolean projectPublic);
}
