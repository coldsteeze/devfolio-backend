package korobkin.nikita.project_service.repository;

import korobkin.nikita.project_service.dto.response.ProjectFeedResponse;
import korobkin.nikita.project_service.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {

    boolean existsByName(String name);

    @Query(
            value = """
                       SELECT new korobkin.nikita.project_service.dto.response.ProjectFeedResponse(
                           p.id,
                           p.name,
                           p.userId,
                           p.shortDescription,
                           p.mainImageUrl,
                           p.createdAt
                       )
                       FROM Project p
                       WHERE p.projectPublic = true
                       ORDER BY p.createdAt DESC
                    """,
            countQuery = """
       SELECT COUNT(p)
       FROM Project p
       WHERE p.projectPublic = true
    """
    )
    Page<ProjectFeedResponse> findFeed(Pageable pageable);

    @Query("SELECT p.userId FROM Project p WHERE p.id = :projectId")
    Optional<UUID> findOwnerIdByProjectId(@Param("projectId") UUID projectId);
}
