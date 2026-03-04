package korobkin.nikita.project_service.service.impl;

import korobkin.nikita.events.ProjectSkillVerificationCompletedEvent;
import korobkin.nikita.events.ProjectSkillVerificationRequestedEvent;
import korobkin.nikita.project_service.client.SkillClient;
import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.dto.response.ProjectDetailsResponse;
import korobkin.nikita.project_service.dto.response.ProjectResponse;
import korobkin.nikita.project_service.dto.response.ProjectSkillResponse;
import korobkin.nikita.project_service.dto.response.VerificationResponse;
import korobkin.nikita.project_service.dto.response.skill.SkillResponse;
import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectSkill;
import korobkin.nikita.project_service.exception.ErrorCode;
import korobkin.nikita.project_service.exception.ProjectAccessDeniedException;
import korobkin.nikita.project_service.exception.ProjectNotFoundException;
import korobkin.nikita.project_service.kafka.producer.SkillEventProducer;
import korobkin.nikita.project_service.mapper.ProjectMapper;
import korobkin.nikita.project_service.repository.ProjectRepository;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import korobkin.nikita.project_service.service.ProjectService;
import korobkin.nikita.project_service.service.ProjectSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectSkillService projectSkillService;
    private final SkillClient skillClient;
    private final SkillEventProducer skillEventProducer;

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, UserPrincipal user) {
        Project project = new Project();
        project.setUserId(user.userId());
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setGithubUrl(request.getGithubUrl());
        project.setProjectPublic(request.isProjectPublic());

        projectRepository.save(project);
        log.info("Project with id {} saved in repository", project.getId());

        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(UpdateProjectRequest request, UUID projectId, UserPrincipal user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getUserId().equals(user.userId())) {
            throw new ProjectAccessDeniedException(ErrorCode.PROJECT_ACCESS_DENIED);
        }

        projectMapper.updateEntityFromDto(request, project);
        project.setUpdatedAt(LocalDateTime.now());
        log.info("Project with id {} updated successfully", project.getId());

        return projectMapper.toDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailsResponse getProject(UUID projectId, UserPrincipal user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.isProjectPublic() && !project.getUserId().equals(user.userId())) {
            throw new ProjectAccessDeniedException(ErrorCode.PROJECT_ACCESS_DENIED);
        }

        log.info("Successfully get project with id {}", project.getId());

        return projectMapper.toDetailsDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getUserProjects(UUID userId, UserPrincipal user) {
        List<Project> projects;

        if (userId.equals(user.userId())) {
            projects = projectRepository.findProjectsByUserId(userId);
        } else {
            projects = projectRepository.findProjectsByUserIdAndProjectPublic(userId, true);
        }

        log.info("Successfully get user projects with user id {}", userId);

        return projectMapper.toDtoList(projects);
    }

    @Override
    @Transactional
    public void deleteProject(UUID projectId, UserPrincipal user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getUserId().equals(user.userId())) {
            throw new ProjectAccessDeniedException(ErrorCode.PROJECT_ACCESS_DENIED);
        }

        log.info("Successfully delete project with id {}", projectId);

        projectRepository.delete(project);
    }

    @Override
    @Transactional
    public ProjectSkillResponse addSkillProject(UUID projectId, UUID skillId, UserPrincipal user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getUserId().equals(user.userId())) {
            throw new ProjectAccessDeniedException(ErrorCode.PROJECT_ACCESS_DENIED);
        }

        return projectSkillService.addForProject(project, skillId, true);
    }

    @Override
    @Transactional
    public void deleteSkillProject(UUID projectId, UUID skillId, UserPrincipal user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getUserId().equals(user.userId())) {
            throw new ProjectAccessDeniedException(ErrorCode.PROJECT_ACCESS_DENIED);
        }

        projectSkillService.deleteForProject(project, skillId);
    }

    @Override
    @Transactional(readOnly = true)
    public VerificationResponse verifySkillProject(UUID projectId, UUID skillId, UserPrincipal user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getUserId().equals(user.userId())) {
            throw new ProjectAccessDeniedException(ErrorCode.PROJECT_ACCESS_DENIED);
        }

        ProjectSkill projectSkill = projectSkillService.findProjectSkillByProjectAndSkill(project, skillId);

        SkillResponse skillResponse = skillClient.getSkillById(projectSkill.getSkillId());

        skillEventProducer.sendVerificationRequest(
                new ProjectSkillVerificationRequestedEvent(
                        projectId, skillId, skillResponse.name(), project.getGithubUrl())
        );

        return new VerificationResponse("VERIFICATION_REQUESTED");
    }

    @Override
    @Transactional
    public void confirmSkillProject(ProjectSkillVerificationCompletedEvent event) {
        if (event.confirmed()) {
            Project project = projectRepository.findById(event.projectId())
                    .orElseThrow(() -> new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND));

            projectSkillService.confirmForProject(project, event.skillId());
            log.info("Confirm skill {} for project {}", event.skillId(), event.projectId());
        } else log.info("Not confirm skill {} for project {}", event.skillId(), event.projectId());
    }

    @Override
    @Transactional
    public List<ProjectSkillResponse> getProjectSkills(UUID projectId, UserPrincipal user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.isProjectPublic() && !project.getUserId().equals(user.userId())) {
            throw new ProjectAccessDeniedException(ErrorCode.PROJECT_ACCESS_DENIED);
        }

        log.info("Get projects skill with project id {}", projectId);

        return projectSkillService.getProjectSkills(project);
    }
}
