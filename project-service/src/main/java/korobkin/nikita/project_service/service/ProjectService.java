package korobkin.nikita.project_service.service;

import korobkin.nikita.events.ProjectSkillVerificationCompletedEvent;
import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.dto.response.ProjectDetailsResponse;
import korobkin.nikita.project_service.dto.response.ProjectResponse;
import korobkin.nikita.project_service.dto.response.ProjectSkillResponse;
import korobkin.nikita.project_service.dto.response.VerificationResponse;
import korobkin.nikita.project_service.security.user.UserPrincipal;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request, UserPrincipal user);

    ProjectResponse updateProject(UpdateProjectRequest request, UUID projectId, UserPrincipal user);

    ProjectDetailsResponse getProject(UUID projectId, UserPrincipal user);

    List<ProjectResponse> getUserProjects(UUID userId, UserPrincipal user);

    void deleteProject(UUID projectId, UserPrincipal user);

    ProjectSkillResponse addSkillProject(UUID projectId, UUID skillId, UserPrincipal user);

    void deleteSkillProject(UUID projectId, UUID skillId, UserPrincipal user);

    VerificationResponse verifySkillProject(UUID projectId, UUID skillId, UserPrincipal user);

    void confirmSkillProject(ProjectSkillVerificationCompletedEvent event);

    List<ProjectSkillResponse> getProjectSkills(UUID projectId, UserPrincipal user);
}
