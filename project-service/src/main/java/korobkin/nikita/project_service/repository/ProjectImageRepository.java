package korobkin.nikita.project_service.repository;

import korobkin.nikita.project_service.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, UUID> {

    Optional<ProjectImage> findByImageUrl(String imageUrl);

    int countByProjectId(UUID projectId);
}
