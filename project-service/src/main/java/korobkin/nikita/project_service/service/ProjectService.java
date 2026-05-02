package korobkin.nikita.project_service.service;

import korobkin.nikita.events.ProjectSkillVerificationCompletedEvent;
import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.ProjectFilterRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.dto.response.*;
import korobkin.nikita.project_service.dto.response.media.MediaResponse;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request, UserPrincipal user);

    ProjectResponse updateProject(UpdateProjectRequest request, UUID projectId, UserPrincipal user);

    ProjectDetailsResponse getProject(UUID projectId, UserPrincipal user);

    PagedResponse<ProjectResponse> getUserProjects(
            UUID userId,
            UserPrincipal user,
            ProjectFilterRequest filter,
            Pageable pageable
    );

    void deleteProject(UUID projectId, UserPrincipal user);

    ProjectSkillResponse addSkillProject(UUID projectId, UUID skillId, UserPrincipal user);

    void deleteSkillProject(UUID projectId, UUID skillId, UserPrincipal user);

    VerificationResponse verifySkillProject(UUID projectId, UserPrincipal user);

    void confirmSkillProject(ProjectSkillVerificationCompletedEvent event);

    List<ProjectSkillResponse> getProjectSkills(UUID projectId, UserPrincipal user);

    PagedResponse<ProjectFeedResponse> getProjectsFeed(Pageable pageable);

    MediaResponse uploadPreviewPhoto(UUID projectId, UserPrincipal currentUser, MultipartFile file);

    MediaResponse uploadProjectPhoto(UUID projectId, UserPrincipal currentUser, MultipartFile file);
}
