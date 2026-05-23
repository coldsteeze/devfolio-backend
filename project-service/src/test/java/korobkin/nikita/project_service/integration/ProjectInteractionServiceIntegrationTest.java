package korobkin.nikita.project_service.integration;

import jakarta.persistence.EntityManager;
import korobkin.nikita.project_service.dto.response.LikeStatusResponse;
import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectLike;
import korobkin.nikita.project_service.entity.ProjectView;
import korobkin.nikita.project_service.exception.ErrorCode;
import korobkin.nikita.project_service.exception.ProjectNotFoundException;
import korobkin.nikita.project_service.fixtures.ProjectFixtures;
import korobkin.nikita.project_service.repository.ProjectLikeRepository;
import korobkin.nikita.project_service.repository.ProjectRepository;
import korobkin.nikita.project_service.repository.ProjectViewRepository;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import korobkin.nikita.project_service.service.ProjectInteractionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectInteractionServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectInteractionService projectInteractionService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectLikeRepository projectLikeRepository;

    @Autowired
    private ProjectViewRepository projectViewRepository;

    @Autowired
    private EntityManager entityManager;

    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
    }

    @Test
    void recordView_shouldSaveViewAndIncrementCounter() {
        Project project = createAndSaveProject();

        UUID viewerId = UUID.randomUUID();

        projectInteractionService.recordView(
                project.getId(),
                new UserPrincipal(viewerId)
        );

        entityManager.flush();
        entityManager.clear();

        assertThat(projectViewRepository.findAll())
                .hasSize(1)
                .extracting(ProjectView::getUserId)
                .containsExactly(viewerId);

        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();

        assertEquals(1L, updatedProject.getViewsCount());
    }

    @Test
    void recordView_shouldIgnore_whenViewerIsOwner() {
        Project project = createAndSaveProject();

        projectInteractionService.recordView(
                project.getId(),
                new UserPrincipal(ownerId)
        );

        assertTrue(projectViewRepository.findAll().isEmpty());

        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();

        assertEquals(0L, updatedProject.getViewsCount());
    }

    @Test
    void recordView_withInvalidProjectId_shouldThrowNotFound() {
        assertThatThrownBy(() -> projectInteractionService.recordView(
                UUID.randomUUID(),
                new UserPrincipal(UUID.randomUUID())
        ))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(ErrorCode.PROJECT_NOT_FOUND.message);
    }

    @Test
    void like_shouldSaveLikeAndIncrementCounter() {
        Project project = createAndSaveProject();

        UUID userId = UUID.randomUUID();

        projectInteractionService.like(
                project.getId(),
                new UserPrincipal(userId)
        );

        entityManager.flush();
        entityManager.clear();

        assertThat(projectLikeRepository.findAll())
                .hasSize(1)
                .extracting(ProjectLike::getUserId)
                .containsExactly(userId);

        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();

        assertEquals(1L, updatedProject.getLikesCount());
    }

    @Test
    void like_shouldBeIdempotent() {
        Project project = createAndSaveProject();

        UUID userId = UUID.randomUUID();

        projectInteractionService.like(
                project.getId(),
                new UserPrincipal(userId)
        );

        projectInteractionService.like(
                project.getId(),
                new UserPrincipal(userId)
        );

        entityManager.flush();
        entityManager.clear();

        assertThat(projectLikeRepository.findAll()).hasSize(1);

        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();

        assertEquals(1L, updatedProject.getLikesCount());
    }

    @Test
    void removeLike_shouldDeleteLikeAndDecrementCounter() {
        Project project = createAndSaveProject();

        UUID userId = UUID.randomUUID();

        projectInteractionService.like(
                project.getId(),
                new UserPrincipal(userId)
        );

        projectInteractionService.removeLike(
                project.getId(),
                new UserPrincipal(userId)
        );

        assertTrue(projectLikeRepository.findAll().isEmpty());

        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();

        assertEquals(0L, updatedProject.getLikesCount());
    }

    @Test
    void removeLike_shouldBeIdempotent() {
        Project project = createAndSaveProject();

        UUID userId = UUID.randomUUID();

        projectInteractionService.removeLike(
                project.getId(),
                new UserPrincipal(userId)
        );

        assertTrue(projectLikeRepository.findAll().isEmpty());

        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();

        assertEquals(0L, updatedProject.getLikesCount());
    }

    @Test
    void isLiked_shouldReturnTrue() {
        Project project = createAndSaveProject();

        UUID userId = UUID.randomUUID();

        projectInteractionService.like(
                project.getId(),
                new UserPrincipal(userId)
        );

        LikeStatusResponse response = projectInteractionService.isLiked(
                project.getId(),
                new UserPrincipal(userId)
        );

        assertTrue(response.liked());
    }

    @Test
    void isLiked_shouldReturnFalse() {
        Project project = createAndSaveProject();

        LikeStatusResponse response = projectInteractionService.isLiked(
                project.getId(),
                new UserPrincipal(UUID.randomUUID())
        );

        assertFalse(response.liked());
    }

    private Project createAndSaveProject() {
        Project project = ProjectFixtures.validProject(ownerId);
        projectRepository.save(project);

        return project;
    }
}