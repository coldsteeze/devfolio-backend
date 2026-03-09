package korobkin.nikita.project_service.integration;

import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.fixtures.ProjectFixtures;
import korobkin.nikita.project_service.repository.ProjectRepository;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void getUserProjects_shouldReturnProjects() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(get("/api/users/" + userId + "/projects")
                        .with(auth(userId)))
                .andExpect(jsonPath("$.content[*].id").value(project.getId().toString()))
                .andExpect(jsonPath("$.content[*].name").value(project.getName()));
    }

    @Test
    void getUserProjects_shouldReturnPublicProjects() throws Exception {
        Project firstProject = ProjectFixtures.projectWithProjectPublicFalse(UUID.randomUUID());
        Project secondProject = ProjectFixtures.validProject(userId);
        projectRepository.save(firstProject);
        projectRepository.save(secondProject);

        mockMvc.perform(get("/api/users/" + userId + "/projects")
                        .with(auth(userId)))
                .andExpect(jsonPath("$.content[*].id").value(secondProject.getId().toString()))
                .andExpect(jsonPath("$.content[*].name").value(secondProject.getName()))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    void getUserProjects_withIncompleteName_shouldReturnProjects() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(get("/api/users/" + userId + "/projects")
                        .with(auth(userId))
                        .param("search", "Proj"))
                .andExpect(jsonPath("$.content[*].id").value(project.getId().toString()))
                .andExpect(jsonPath("$.content[*].name").value(project.getName()))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    void getUserProjects_shouldReturnPaginatedResponse() throws Exception {
        for (int i = 0; i < 15; i++) {
            Project project = ProjectFixtures.projectWithCustomName(userId, "Project " + i);
            projectRepository.save(project);
        }

        mockMvc.perform(get("/api/users/" + userId + "/projects")
                        .param("page", "0")
                        .param("size", "10")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Project 0"))
                .andExpect(jsonPath("$.content[9].name").value("Project 9"));

        mockMvc.perform(get("/api/users/" + userId + "/projects")
                        .param("page", "1")
                        .param("size", "10")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Project 10"))
                .andExpect(jsonPath("$.content[4].name").value("Project 14"));

        mockMvc.perform(get("/api/users/" + userId + "/projects")
                        .param("page", "999")
                        .param("size", "10")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(15));
    }

    @Test
    void getUserProjects_withSearchAndPagination_shouldReturnFilteredPage() throws Exception {
        for (int i = 0; i < 10; i++) {
            projectRepository.save(ProjectFixtures.projectWithCustomName(userId, "Java Project " + i));
            projectRepository.save(ProjectFixtures.projectWithCustomName(userId, "Python Project " + i));
        }

        // Запрашиваем первую страницу проектов с "Java"
        mockMvc.perform(get("/api/users/" + userId + "/projects")
                        .param("search", "Java")
                        .param("page", "0")
                        .param("size", "5")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.content[*].name").value(everyItem(containsString("Java"))));
    }

    @Test
    void getUserProjects_shouldReturnOnlyCurrentUserProjects() throws Exception {
        UUID otherUserId = UUID.randomUUID();
        for (int i = 0; i < 5; i++) {
            projectRepository.save(ProjectFixtures.validProject(otherUserId));
        }

        for (int i = 0; i < 5; i++) {
            projectRepository.save(ProjectFixtures.validProject(userId));
        }

        mockMvc.perform(get("/api/users/" + userId + "/projects")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    private RequestPostProcessor auth(UUID userId) {
        return authentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(userId),
                null,
                Collections.emptyList()
        ));
    }
}
