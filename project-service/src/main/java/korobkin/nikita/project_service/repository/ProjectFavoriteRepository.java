package korobkin.nikita.project_service.repository;

import korobkin.nikita.project_service.dto.response.ProjectFavoriteResponse;
import korobkin.nikita.project_service.entity.ProjectFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProjectFavoriteRepository extends JpaRepository<ProjectFavorite, UUID> {

    Optional<ProjectFavorite> findProjectFavoriteByProjectIdAndUserId(UUID projectId, UUID userId);

    boolean existsByProjectIdAndUserId(UUID projectId, UUID uuid);

    @Query("SELECT new korobkin.nikita.project_service.dto.response.ProjectFavoriteResponse(f.id, f.projectId, f.createdAt) " +
            "FROM ProjectFavorite f WHERE f.userId = :userId ORDER BY f.createdAt DESC")
    Page<ProjectFavoriteResponse> findProjectFavoritesByUserId(@Param("userId") UUID userId, Pageable pageable);
}
