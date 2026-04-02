package korobkin.nikita.project_service.integration;

import feign.FeignException;
import feign.Request;
import korobkin.nikita.events.ProjectSkillVerificationCompletedEvent;
import korobkin.nikita.events.ProjectSkillVerificationRequestedEvent;
import korobkin.nikita.events.skill.SkillVerificationResult;
import korobkin.nikita.project_service.client.SkillClient;
import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.ProjectFilterRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.dto.response.*;
import korobkin.nikita.project_service.dto.response.skill.SkillResponse;
import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectSkill;
import korobkin.nikita.project_service.entity.enums.SkillCategory;
import korobkin.nikita.project_service.exception.*;
import korobkin.nikita.project_service.fixtures.ProjectFixtures;
import korobkin.nikita.project_service.fixtures.ProjectRequestFixtures;
import korobkin.nikita.project_service.fixtures.ProjectSkillFixtures;
import korobkin.nikita.project_service.kafka.producer.SkillEventProducer;
import korobkin.nikita.project_service.repository.ProjectRepository;
import korobkin.nikita.project_service.repository.ProjectSkillRepository;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import korobkin.nikita.project_service.service.ProjectService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ProjectServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectSkillRepository projectSkillRepository;

    @MockitoBean
    private SkillClient skillClient;

    @MockitoSpyBean
    private SkillEventProducer skillEventProducer;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void createProject_shouldReturnCreatedProject() {
        Project project = ProjectFixtures.validProject(userId);
        CreateProjectRequest createProjectRequest = ProjectRequestFixtures.createProjectRequest(project);

        ProjectResponse projectResponse = projectService.createProject(
                createProjectRequest,
                new UserPrincipal(userId)
        );

        assertThat(projectResponse.name()).isEqualTo(project.getName());
        assertThat(projectResponse.description()).isEqualTo(project.getDescription());
        assertThat(projectResponse.githubUrl()).isEqualTo(project.getGithubUrl());
        assertThat(projectResponse.projectPublic()).isEqualTo(project.isProjectPublic());
    }

    @Test
    void createProject_withExistsName_shouldReturnConflict() {
        Project project = createAndSaveProject();

        assertThatThrownBy(() -> projectService.createProject(
                ProjectRequestFixtures.createProjectRequest(project), new UserPrincipal(userId)))
                .isInstanceOf(ProjectAlreadyExistsException.class)
                .hasMessageContaining(ErrorCode.PROJECT_ALREADY_EXISTS.message);
    }

    @Test
    void updateProject_shouldReturnUpdatedProject() {
        Project project = createAndSaveProject();

        Project updatedProject = ProjectFixtures.updatedValidProject(userId);
        UpdateProjectRequest updateProjectRequest = ProjectRequestFixtures.updateProjectRequest(updatedProject);

        ProjectResponse projectResponse = projectService.updateProject(
                updateProjectRequest,
                project.getId(),
                new UserPrincipal(userId)
        );

        assertThat(projectResponse.name()).isEqualTo(updatedProject.getName());
        assertThat(projectResponse.description()).isEqualTo(updatedProject.getDescription());
        assertThat(projectResponse.githubUrl()).isEqualTo(updatedProject.getGithubUrl());
        assertThat(projectResponse.projectPublic()).isEqualTo(updatedProject.isProjectPublic());
        assertThat(projectResponse.updatedAt()).isNotNull();
    }

    @Test
    void updateProject_withInvalidId_shouldReturnNotFound() {
        Project updatedProject = ProjectFixtures.updatedValidProject(userId);

        assertThatThrownBy(() -> projectService.updateProject(
                ProjectRequestFixtures.updateProjectRequest(updatedProject),
                UUID.randomUUID(),
                new UserPrincipal(userId)))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(ErrorCode.PROJECT_NOT_FOUND.message);
    }

    @Test
    void updateProject_withInvalidUserId_shouldReturnAccessDenied() {
        Project project = createAndSaveProject();

        Project updatedProject = ProjectFixtures.updatedValidProject(userId);
        UpdateProjectRequest updateProjectRequest = ProjectRequestFixtures.updateProjectRequest(updatedProject);

        assertThatThrownBy(() -> projectService.updateProject(
                updateProjectRequest,
                project.getId(),
                new UserPrincipal(UUID.randomUUID())))
                .isInstanceOf(ProjectAccessDeniedException.class)
                .hasMessageContaining(ErrorCode.PROJECT_ACCESS_DENIED.message);
    }

    @Test
    void getProject_shouldReturnProjectDetails() {
        Project project = createAndSaveProject();

        ProjectSkill projectSkill = createAndSaveProjectSkill(project, UUID.randomUUID());

        project.getSkills().add(projectSkill);
        projectRepository.save(project);

        ProjectDetailsResponse projectDetailsResponse = projectService.getProject(
                project.getId(),
                new UserPrincipal(userId)
        );

        assertThat(projectDetailsResponse.project().name()).isEqualTo(project.getName());
        assertThat(projectDetailsResponse.project().description()).isEqualTo(project.getDescription());
        assertThat(projectDetailsResponse.project().githubUrl()).isEqualTo(project.getGithubUrl());
        assertThat(projectDetailsResponse.project().projectPublic()).isEqualTo(project.isProjectPublic());
        assertThat(projectDetailsResponse.skills())
                .extracting(ProjectSkillResponse::skillId)
                .containsExactly(projectSkill.getSkillId());
    }

    @Test
    void getProject_withInvalidId_shouldReturnNotFound() {
        assertThatThrownBy(() -> projectService.getProject(
                UUID.randomUUID(),
                new UserPrincipal(userId)))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(ErrorCode.PROJECT_NOT_FOUND.message);
    }

    @Test
    void getProject_withInvalidUserId_shouldReturnAccessDenied() {
        Project project = ProjectFixtures.projectWithProjectPublicFalse(userId);
        projectRepository.save(project);

        assertThatThrownBy(() -> projectService.getProject(
                project.getId(),
                new UserPrincipal(UUID.randomUUID())))
                .isInstanceOf(ProjectAccessDeniedException.class)
                .hasMessageContaining(ErrorCode.PROJECT_ACCESS_DENIED.message);
    }

    @Test
    void getUserProjects_shouldReturnProjects() {
        Project firstProject = ProjectFixtures.projectWithProjectPublicFalse(userId);
        Project secondProject = ProjectFixtures.projectWithCustomName(userId, "Custom");
        projectRepository.save(firstProject);
        projectRepository.save(secondProject);

        PagedResponse<ProjectResponse> projects = projectService.getUserProjects(
                userId,
                new UserPrincipal(userId),
                new ProjectFilterRequest(),
                PageRequest.of(0, 10)
        );

        Assertions.assertThat(projects.content())
                .extracting(ProjectResponse::name)
                .containsExactly(firstProject.getName(), secondProject.getName());
    }

    @Test
    void getUserProjects_shouldReturnPublicProjects() {
        Project firstProject = ProjectFixtures.projectWithProjectPublicFalse(userId);
        Project secondProject = ProjectFixtures.projectWithCustomName(userId, "Custom");
        projectRepository.save(firstProject);
        projectRepository.save(secondProject);

        PagedResponse<ProjectResponse> projects = projectService.getUserProjects(
                userId,
                new UserPrincipal(UUID.randomUUID()),
                new ProjectFilterRequest(),
                PageRequest.of(0, 10)
        );

        Assertions.assertThat(projects.content())
                .extracting(ProjectResponse::name)
                .containsExactly(secondProject.getName());
    }

    @Test
    void getUserProjects_withIncompleteName_shouldProjects() {
        Project firstProject = ProjectFixtures.projectWithCustomName(userId, "Custom 1");
        Project secondProject = ProjectFixtures.projectWithCustomName(userId, "Custom 2");
        projectRepository.save(firstProject);
        projectRepository.save(secondProject);

        PagedResponse<ProjectResponse> projects = projectService.getUserProjects(
                userId,
                new UserPrincipal(userId),
                ProjectRequestFixtures.projectFilterRequest("Cus",
                        true,
                        null,
                        null),
                PageRequest.of(0, 10)
        );

        Assertions.assertThat(projects.content())
                .extracting(ProjectResponse::name)
                .containsExactly(firstProject.getName(), secondProject.getName());
    }

    @Test
    void getUserProject_shouldReturnProjectsBeforeCreated() {
        Project firstProject = ProjectFixtures.projectWithCustomName(userId, "Custom 1");
        Project secondProject = ProjectFixtures.projectWithCustomName(userId, "Custom 2");
        projectRepository.save(firstProject);
        projectRepository.save(secondProject);

        PagedResponse<ProjectResponse> projects = projectService.getUserProjects(
                userId,
                new UserPrincipal(userId),
                ProjectRequestFixtures.projectFilterRequest(
                        null,
                        true, null,
                        firstProject.getCreatedAt()),
                PageRequest.of(0, 10)
        );

        Assertions.assertThat(projects.content())
                .extracting(ProjectResponse::name)
                .containsExactly(firstProject.getName());
    }

    @Test
    void getUserProject_withSortByNameAsc_shouldReturnSortedProjects() {
        Project firstProject = ProjectFixtures.projectWithCustomName(userId, "B");
        Project secondProject = ProjectFixtures.projectWithCustomName(userId, "A");
        projectRepository.save(firstProject);
        projectRepository.save(secondProject);

        PagedResponse<ProjectResponse> projects = projectService.getUserProjects(
                userId,
                new UserPrincipal(userId),
                new ProjectFilterRequest(),
                PageRequest.of(0, 10, Sort.by("name").ascending())
        );

        Assertions.assertThat(projects.content())
                .extracting(ProjectResponse::name)
                .containsExactly(secondProject.getName(), firstProject.getName());
    }

    @Test
    void getUserProject_withPageable_shouldReturnCorrectPage() {
        for (int i = 0; i < 15; i++) {
            projectRepository.save(ProjectFixtures.projectWithCustomName(userId, "Project " + i));
        }

        PagedResponse<ProjectResponse> projects = projectService.getUserProjects(
                userId,
                new UserPrincipal(userId),
                new ProjectFilterRequest(),
                PageRequest.of(0, 10)
        );

        Assertions.assertThat(projects.content()).hasSize(10);
        Assertions.assertThat(projects.totalElements()).isEqualTo(15);
        Assertions.assertThat(projects.pageNumber()).isEqualTo(0);
        Assertions.assertThat(projects.totalPages()).isEqualTo(2);
    }

    @Test
    void deleteProject_shouldUpdateDatabase() {
        Project project = createAndSaveProject();

        projectService.deleteProject(project.getId(), new UserPrincipal(userId));

        assertThat(projectRepository.existsByName(project.getName())).isFalse();
    }

    @Test
    void deleteProject_withInvalidId_shouldReturnNotFound() {
        assertThatThrownBy(() -> projectService.deleteProject(UUID.randomUUID(), new UserPrincipal(userId)))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(ErrorCode.PROJECT_NOT_FOUND.message);
    }

    @Test
    void deleteProject_withInvalidUserId_shouldReturnAccessDenied() {
        Project project = createAndSaveProject();

        assertThatThrownBy(() -> projectService.deleteProject(project.getId(), new UserPrincipal(UUID.randomUUID())))
                .isInstanceOf(ProjectAccessDeniedException.class)
                .hasMessageContaining(ErrorCode.PROJECT_ACCESS_DENIED.message);
    }

    @Test
    void addSkillProject_shouldReturnProjectSkill() {
        Project project = createAndSaveProject();

        UUID skillId = UUID.randomUUID();
        when(skillClient.getSkillById(skillId)).thenReturn(new SkillResponse(skillId, "Java", SkillCategory.LANGUAGE));

        ProjectSkillResponse projectSkillResponse = projectService.addSkillProject(
                project.getId(),
                skillId,
                new UserPrincipal(userId)
        );

        assertThat(projectSkillResponse.skillId()).isEqualTo(skillId);
        assertThat(projectSkillResponse.confirmed()).isFalse();
        assertThat(projectSkillResponse.manuallyAdded()).isTrue();
    }

    @Test
    void addSkillProject_withInvalidId_shouldReturnNotFound() {
        assertThatThrownBy(() -> projectService.addSkillProject(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new UserPrincipal(userId)))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(ErrorCode.PROJECT_NOT_FOUND.message);
    }

    @Test
    void addSkillProject_withInvalidUserId_shouldReturnAccessDenied() {
        Project project = createAndSaveProject();

        assertThatThrownBy(() -> projectService.addSkillProject(
                project.getId(),
                UUID.randomUUID(),
                new UserPrincipal(UUID.randomUUID())))
                .isInstanceOf(ProjectAccessDeniedException.class)
                .hasMessageContaining(ErrorCode.PROJECT_ACCESS_DENIED.message);
    }

    @Test
    void addSkillProject_withExistsProjectSkill_shouldReturnAlreadyExists() {
        Project project = createAndSaveProject();

        UUID skillId = UUID.randomUUID();
        createAndSaveProjectSkill(project, skillId);

        assertThatThrownBy(() -> projectService.addSkillProject(
                project.getId(),
                skillId,
                new UserPrincipal(userId)))
                .isInstanceOf(ProjectSkillAlreadyExistsException.class)
                .hasMessageContaining(ErrorCode.PROJECT_SKILL_ALREADY_EXISTS.message);
    }

    @Test
    void addSkillProject_withInvalidSkillId_shouldReturnNotFound() {
        Project project = createAndSaveProject();

        UUID skillId = UUID.randomUUID();
        when(skillClient.getSkillById(skillId)).thenThrow(new FeignException.NotFound(
                "Not found",
                Request.create(Request.HttpMethod.GET, "/skills/" + skillId, new HashMap<>(), null, null, null),
                null,
                null
        ));

        assertThatThrownBy(() -> projectService.addSkillProject(
                project.getId(),
                skillId,
                new UserPrincipal(userId)))
                .isInstanceOf(SkillNotFoundException.class)
                .hasMessageContaining(ErrorCode.SKILL_NOT_FOUND.message);
    }

    @Test
    void deleteSkillProject_shouldUpdateDatabase() {
        Project project = createAndSaveProject();

        UUID skillId = UUID.randomUUID();
        createAndSaveProjectSkill(project, skillId);

        projectService.deleteSkillProject(project.getId(), skillId, new UserPrincipal(userId));

        assertThat(projectSkillRepository.existsByProjectAndSkillId(project, skillId)).isFalse();
    }

    @Test
    void deleteSkillProject_withInvalidId_shouldReturnNotFound() {
        assertThatThrownBy(() -> projectService.deleteSkillProject(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new UserPrincipal(userId)))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(ErrorCode.PROJECT_NOT_FOUND.message);
    }

    @Test
    void deleteSkillProject_withInvalidUserId_shouldReturnAccessDenied() {
        Project project = createAndSaveProject();

        assertThatThrownBy(() -> projectService.deleteSkillProject(
                project.getId(),
                UUID.randomUUID(),
                new UserPrincipal(UUID.randomUUID())))
                .isInstanceOf(ProjectAccessDeniedException.class)
                .hasMessageContaining(ErrorCode.PROJECT_ACCESS_DENIED.message);
    }

    @Test
    void deleteSkillProject_withInvalidSkillId_shouldReturnNotFound() {
        Project project = createAndSaveProject();

        assertThatThrownBy(() -> projectService.deleteSkillProject(
                project.getId(),
                UUID.randomUUID(),
                new UserPrincipal(userId)))
                .isInstanceOf(ProjectSkillNotFoundException.class)
                .hasMessageContaining(ErrorCode.PROJECT_SKILL_NOT_FOUND.message);
    }

    @Test
    void verifySkillProject_shouldReturnVerificationResponse() {
        Project project = createAndSaveProject();

        UUID skillId = UUID.randomUUID();
        createAndSaveProjectSkill(project, skillId);

        when(skillClient.getSkillById(skillId)).thenReturn(new SkillResponse(skillId, "Java", SkillCategory.LANGUAGE));

        VerificationResponse verificationResponse = projectService.verifySkillProject(
                project.getId(),
                new UserPrincipal(userId)
        );

        assertThat(verificationResponse.status()).isEqualTo("VERIFICATION_REQUESTED");

        Mockito.verify(skillEventProducer).sendVerificationRequest(any(ProjectSkillVerificationRequestedEvent.class));
    }

    @Test
    void verifySkillProject_withInvalidId_shouldReturnNotFound() {
        assertThatThrownBy(() -> projectService.verifySkillProject(
                UUID.randomUUID(),
                new UserPrincipal(userId)))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(ErrorCode.PROJECT_NOT_FOUND.message);
    }

    @Test
    void verifySkillProject_withInvalidUserId_shouldReturnAccessDenied() {
        Project project = createAndSaveProject();

        assertThatThrownBy(() -> projectService.verifySkillProject(
                project.getId(),
                new UserPrincipal(UUID.randomUUID())))
                .isInstanceOf(ProjectAccessDeniedException.class)
                .hasMessageContaining(ErrorCode.PROJECT_ACCESS_DENIED.message);
    }

    @Test
    void confirmSkillProject_shouldUpdateDatabase() {
        Project project = createAndSaveProject();

        UUID skillId = UUID.randomUUID();
        ProjectSkill projectSkill = createAndSaveProjectSkill(project, skillId);

        projectService.confirmSkillProject(
                new ProjectSkillVerificationCompletedEvent(
                        project.getId(),
                        List.of(new SkillVerificationResult(projectSkill.getId(), true)))
        );

        assertThat(projectSkill.isConfirmed()).isTrue();
    }

    @Test
    void confirmSkillProject_withConfirmedFalse_shouldNotUpdateDatabase() {
        Project project = createAndSaveProject();

        UUID skillId = UUID.randomUUID();
        ProjectSkill projectSkill = createAndSaveProjectSkill(project, skillId);

        projectService.confirmSkillProject(
                new ProjectSkillVerificationCompletedEvent(
                        project.getId(),
                        List.of(new SkillVerificationResult(skillId, false)))
        );

        assertThat(projectSkill.isConfirmed()).isFalse();
    }

    @Test
    void getProjectSkills_shouldReturnSkills() {
        Project project = createAndSaveProject();

        UUID skillId = UUID.randomUUID();
        createAndSaveProjectSkill(project, skillId);

        List<ProjectSkillResponse> skillResponses = projectService.getProjectSkills(
                project.getId(),
                new UserPrincipal(userId)
        );

        assertThat(skillResponses.get(0).skillId()).isEqualTo(skillId);
    }

    @Test
    void getProjectSkills_withInvalidId_shouldReturnNotFound() {
        assertThatThrownBy(() -> projectService.getProjectSkills(
                UUID.randomUUID(),
                new UserPrincipal(userId)))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(ErrorCode.PROJECT_NOT_FOUND.message);
    }

    @Test
    void getProjectSkills_withInvalidUserId_shouldReturnAccessDenied() {
        Project project = ProjectFixtures.projectWithProjectPublicFalse(userId);
        projectRepository.save(project);

        assertThatThrownBy(() -> projectService.getProjectSkills(
                project.getId(),
                new UserPrincipal(UUID.randomUUID())))
                .isInstanceOf(ProjectAccessDeniedException.class)
                .hasMessageContaining(ErrorCode.PROJECT_ACCESS_DENIED.message);
    }

    private Project createAndSaveProject() {
        Project project = ProjectFixtures.validProject(userId);
        projectRepository.save(project);

        return project;
    }

    private ProjectSkill createAndSaveProjectSkill(Project project, UUID skillId) {
        ProjectSkill projectSkill = ProjectSkillFixtures.validProjectSkill(project, skillId);
        projectSkillRepository.save(projectSkill);

        return projectSkill;
    }
}
