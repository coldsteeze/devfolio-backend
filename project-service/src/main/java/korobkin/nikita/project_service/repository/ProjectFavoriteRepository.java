package korobkin.nikita.project_service.repository;

import korobkin.nikita.project_service.entity.ProjectFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectFavoriteRepository extends JpaRepository<ProjectFavorite, UUID> {

    Optional<ProjectFavorite> findProjectFavoriteByProjectIdAndUserId(UUID projectId, UUID userId);

    boolean existsByProjectIdAndUserId(UUID projectId, UUID uuid);
}
