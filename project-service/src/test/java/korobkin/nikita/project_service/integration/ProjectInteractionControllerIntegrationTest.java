package korobkin.nikita.project_service.integration;

import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectLike;
import korobkin.nikita.project_service.fixtures.ProjectFixtures;
import korobkin.nikita.project_service.fixtures.ProjectLikeFixtures;
import korobkin.nikita.project_service.repository.ProjectLikeRepository;
import korobkin.nikita.project_service.repository.ProjectRepository;
import korobkin.nikita.project_service.repository.ProjectViewRepository;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "services.skill-service.url=http://localhost:${wiremock.server.port}"
})
public class ProjectInteractionControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectLikeRepository projectLikeRepository;

    @Autowired
    private ProjectViewRepository projectViewRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void recordView_shouldCreateViewAndReturn204() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(post("/api/projects/{projectId}/view", project.getId())
                        .with(auth(UUID.randomUUID())))
                .andExpect(status().isNoContent());

        assertFalse(projectViewRepository.findAll().isEmpty());
    }

    @Test
    void like_shouldCreateLikeAndReturn204() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(post("/api/projects/{projectId}/like", project.getId())
                        .with(auth(userId)))
                .andExpect(status().isNoContent());

        assertTrue(
                projectLikeRepository.existsByProjectIdAndUserId(
                        project.getId(),
                        userId
                )
        );
    }

    @Test
    void unlike_shouldRemoveLikeAndReturn204() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        ProjectLike projectLike = ProjectLikeFixtures.projectLike(userId, project.getId());
        projectLikeRepository.save(projectLike);

        mockMvc.perform(delete("/api/projects/{projectId}/like", project.getId())
                        .with(auth(userId)))
                .andExpect(status().isNoContent());

        assertFalse(
                projectLikeRepository.existsByProjectIdAndUserId(
                        project.getId(),
                        userId
                )
        );
    }

    @Test
    void getLikeStatus_shouldReturnTrue_whenProjectLiked() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        ProjectLike projectLike = ProjectLikeFixtures.projectLike(userId, project.getId());
        projectLikeRepository.save(projectLike);

        mockMvc.perform(get("/api/projects/{projectId}/like/status", project.getId())
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true));
    }

    @Test
    void getLikeStatus_shouldReturnFalse_whenProjectNotLiked() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(get("/api/projects/{projectId}/like/status", project.getId())
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false));
    }

    private RequestPostProcessor auth(UUID userId) {
        return authentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(userId),
                null,
                Collections.emptyList()
        ));
    }
}
