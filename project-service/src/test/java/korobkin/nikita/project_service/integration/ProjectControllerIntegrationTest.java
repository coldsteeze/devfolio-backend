package korobkin.nikita.project_service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectSkill;
import korobkin.nikita.project_service.exception.ErrorCode;
import korobkin.nikita.project_service.fixtures.ProjectFixtures;
import korobkin.nikita.project_service.fixtures.ProjectRequestFixtures;
import korobkin.nikita.project_service.fixtures.ProjectSkillFixtures;
import korobkin.nikita.project_service.repository.ProjectRepository;
import korobkin.nikita.project_service.repository.ProjectSkillRepository;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "services.skill-service.url=http://localhost:${wiremock.server.port}"
})
public class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectSkillRepository projectSkillRepository;

    private UUID userId;
    private UUID skillId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        skillId = UUID.randomUUID();
    }

    @Test
    void createProject_shouldReturnCreatedProject() throws Exception {
        Project project = ProjectFixtures.validProject(userId);

        mockMvc.perform(post("/api/projects")
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(ProjectRequestFixtures.createProjectRequest(project))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(project.getName()))
                .andExpect(jsonPath("$.description").value(project.getDescription()))
                .andExpect(jsonPath("$.githubUrl").value(project.getGithubUrl()))
                .andExpect(jsonPath("$.projectPublic").value(project.isProjectPublic()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t"})
    void createProject_withBlankName_shouldReturnBadRequest(String invalidName) throws Exception {
        Project project = ProjectFixtures.projectWithInvalidName(invalidName);

        mockMvc.perform(post("/api/projects")
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(ProjectRequestFixtures.createProjectRequest(project))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createProject_withExistsName_shouldReturnConflict() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(post("/api/projects")
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(ProjectRequestFixtures.createProjectRequest(project))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_ALREADY_EXISTS.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_ALREADY_EXISTS.name()));
    }

    @Test
    void updateProject_shouldReturnUpdatedProject() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(put("/api/projects/" + project.getId())
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(ProjectRequestFixtures.updateProjectRequest(ProjectFixtures.updatedValidProject(userId)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId().toString()))
                .andExpect(jsonPath("$.name").value(project.getName()))
                .andExpect(jsonPath("$.description").value(project.getDescription()))
                .andExpect(jsonPath("$.githubUrl").value(project.getGithubUrl()))
                .andExpect(jsonPath("$.projectPublic").value(project.isProjectPublic()))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t"})
    void updateProject_withBlankName_shouldReturnBadRequest(String invalidName) throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(put("/api/projects/" + project.getId())
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(ProjectRequestFixtures.updateProjectRequest(ProjectFixtures.projectWithInvalidName(invalidName)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void updateProject_shouldReturnAccessDenied() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(put("/api/projects/" + project.getId())
                        .with(auth(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(ProjectRequestFixtures.updateProjectRequest(ProjectFixtures.validProject(userId)))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_ACCESS_DENIED.message));
    }

    @Test
    void updateProject_shouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/api/projects/" + UUID.randomUUID())
                .with(auth(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(ProjectRequestFixtures.updateProjectRequest(ProjectFixtures.validProject(userId)))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_NOT_FOUND.message));
    }

    @Test
    void getProject_shouldReturnProject() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(get("/api/projects/" + project.getId())
                .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.project.id").value(project.getId().toString()))
                .andExpect(jsonPath("$.project.name").value(project.getName()))
                .andExpect(jsonPath("$.project.description").value(project.getDescription()))
                .andExpect(jsonPath("$.project.githubUrl").value(project.getGithubUrl()))
                .andExpect(jsonPath("$.project.projectPublic").value(project.isProjectPublic()))
                .andExpect(jsonPath("$.skills").exists());
    }

    @Test
    void getProject_shouldReturnAccessDenied() throws Exception {
        Project project = ProjectFixtures.projectWithProjectPublicFalse(userId);
        projectRepository.save(project);

        mockMvc.perform(get("/api/projects/" + project.getId())
                        .with(auth(UUID.randomUUID())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_ACCESS_DENIED.message));
    }

    @Test
    void getProject_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/projects/" + UUID.randomUUID())
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_NOT_FOUND.message));
    }

    @Test
    void deleteProject_shouldReturnNoContent() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(delete("/api/projects/" + project.getId())
                .with(auth(userId)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProject_shouldReturnAccessDenied() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(delete("/api/projects/" + project.getId())
                        .with(auth(UUID.randomUUID())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_ACCESS_DENIED.message));
    }

    @Test
    void deleteProject_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/projects/" + UUID.randomUUID())
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_NOT_FOUND.message));
    }

    @Test
    void addSkillProject_shouldReturnCreatedProjectSkill() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        ProjectSkill projectSkill = ProjectSkillFixtures.validProjectSkill(project, skillId);

        stubFor(WireMock.get(urlPathEqualTo("/api/skills/" + skillId))
                .willReturn(okJson("""
            {
                "id": "%s",
                "name": "Java"
            }
            """.formatted(skillId))));

        mockMvc.perform(post("/api/projects/" + project.getId() + "/skills/" + projectSkill.getSkillId())
                .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillId").value(projectSkill.getSkillId().toString()))
                .andExpect(jsonPath("$.confirmed").value(projectSkill.isConfirmed()))
                .andExpect(jsonPath("$.manuallyAdded").value(projectSkill.isManuallyAdded()));
    }

    @Test
    void addSkillProject_shouldReturnAccessDenied() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(post("/api/projects/" + project.getId() + "/skills/" + skillId)
                        .with(auth(UUID.randomUUID())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_ACCESS_DENIED.message));
    }

    @Test
    void addSkillProject_shouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/api/projects/" + UUID.randomUUID() + "/skills/" + skillId)
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_NOT_FOUND.message));
    }

    @Test
    void addSkillProject_shouldReturnSkillNotFound() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        stubFor(WireMock.get(urlPathEqualTo("/api/skills/" + skillId))
                .willReturn(notFound()));

        mockMvc.perform(post("/api/projects/" + project.getId() + "/skills/" + skillId)
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.SKILL_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.SKILL_NOT_FOUND.message));
    }

    @Test
    void addSkillProject_shouldReturnSkillAlreadyExists() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        ProjectSkill projectSkill = ProjectSkillFixtures.validProjectSkill(project, skillId);
        projectSkillRepository.save(projectSkill);

        mockMvc.perform(post("/api/projects/" + project.getId() + "/skills/" + skillId)
                        .with(auth(userId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_SKILL_ALREADY_EXISTS.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_SKILL_ALREADY_EXISTS.message));
    }

    @Test
    void deleteSkillProject_shouldReturnNoContent() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        ProjectSkill projectSkill = ProjectSkillFixtures.validProjectSkill(project, skillId);
        projectSkillRepository.save(projectSkill);

        mockMvc.perform(delete("/api/projects/" + project.getId() + "/skills/" + skillId)
                        .with(auth(userId)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSkillProject_shouldReturnAccessDenied() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        ProjectSkill projectSkill = ProjectSkillFixtures.validProjectSkill(project, skillId);
        projectSkillRepository.save(projectSkill);

        mockMvc.perform(delete("/api/projects/" + project.getId() + "/skills/" + skillId)
                        .with(auth(UUID.randomUUID())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_ACCESS_DENIED.message));
    }

    @Test
    void deleteSkillProject_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/projects/" + UUID.randomUUID() + "/skills/" + skillId)
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_NOT_FOUND.message));
    }

    @Test
    void deleteSkillProject_shouldReturnSkillNotFound() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(delete("/api/projects/" + project.getId() + "/skills/" + skillId)
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_SKILL_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_SKILL_NOT_FOUND.message));
    }

    @Test
    void verifySkillProject_shouldReturnVerificationResponse() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        ProjectSkill projectSkill = ProjectSkillFixtures.validProjectSkill(project, skillId);
        projectSkillRepository.save(projectSkill);

        stubFor(WireMock.get(urlPathEqualTo("/api/skills/" + skillId))
                .willReturn(okJson("""
            {
                "id": "%s",
                "name": "Java"
            }
            """.formatted(skillId))));

        mockMvc.perform(post("/api/projects/" + project.getId() + "/skills/" + skillId + "/verify")
                        .with(auth(userId)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("VERIFICATION_REQUESTED"));
    }

    @Test
    void verifySkillProject_shouldReturnAccessDenied() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        mockMvc.perform(post("/api/projects/" + project.getId() + "/skills/" + skillId + "/verify")
                        .with(auth(UUID.randomUUID())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_ACCESS_DENIED.message));
    }

    @Test
    void verifySkillProject_shouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/api/projects/" + UUID.randomUUID() + "/skills/" + skillId + "/verify")
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_NOT_FOUND.message));
    }

    @Test
    void getProjectSkills_shouldReturnSkills() throws Exception {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        UUID secondSkillId = UUID.randomUUID();

        ProjectSkill firstSkill = ProjectSkillFixtures.validProjectSkill(project, skillId);
        ProjectSkill secondSkill = ProjectSkillFixtures.validProjectSkill(project, secondSkillId);
        projectSkillRepository.save(firstSkill);
        projectSkillRepository.save(secondSkill);

        mockMvc.perform(get("/api/projects/" + project.getId() + "/skills")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].skillId",
                        containsInAnyOrder(skillId.toString(), secondSkillId.toString())));
    }

    @Test
    void getProjectSkills_shouldReturnAccessDenied() throws Exception {
        Project project = ProjectFixtures.projectWithProjectPublicFalse(userId);
        projectRepository.save(project);

        mockMvc.perform(get("/api/projects/" + project.getId() + "/skills")
                        .with(auth(UUID.randomUUID())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_ACCESS_DENIED.message));
    }

    @Test
    void getProjectSkills_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/projects/" + UUID.randomUUID() + "/skills")
                        .with(auth(UUID.randomUUID())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PROJECT_NOT_FOUND.message));
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    private RequestPostProcessor auth(UUID userId) {
        return authentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(userId),
                null,
                Collections.emptyList()
        ));
    }
}
