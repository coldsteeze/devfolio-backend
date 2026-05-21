package korobkin.nikita.project_service.repository;

import korobkin.nikita.project_service.entity.ProjectLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectLikeRepository extends JpaRepository<ProjectLike, UUID> {

    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);

    int deleteByProjectIdAndUserId(UUID projectId, UUID userId);
}
