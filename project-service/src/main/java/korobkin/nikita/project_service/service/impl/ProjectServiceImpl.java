package korobkin.nikita.project_service.service.impl;

import feign.FeignException;
import jakarta.persistence.criteria.Predicate;
import korobkin.nikita.events.*;
import korobkin.nikita.events.skill.SkillVerificationResult;
import korobkin.nikita.project_service.client.MediaClient;
import korobkin.nikita.project_service.config.ProjectImageProperties;
import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.ProjectFilterRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.dto.response.*;
import korobkin.nikita.project_service.dto.response.media.MediaResponse;
import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectImage;
import korobkin.nikita.project_service.entity.ProjectSkill;
import korobkin.nikita.project_service.exception.*;
import korobkin.nikita.project_service.kafka.producer.*;
import korobkin.nikita.project_service.mapper.ProjectMapper;
import korobkin.nikita.project_service.mapper.ProjectSkillMapper;
import korobkin.nikita.project_service.mapper.SkillEventMapper;
import korobkin.nikita.project_service.repository.ProjectImageRepository;
import korobkin.nikita.project_service.repository.ProjectRepository;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import korobkin.nikita.project_service.service.ProjectService;
import korobkin.nikita.project_service.service.ProjectSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectSkillService projectSkillService;
    private final SkillEventProducer skillEventProducer;
    private final SkillEventMapper skillEventMapper;
    private final ProjectCreatedEventProducer projectCreatedEventProducer;
    private final ProjectUpdatedEventProducer projectUpdatedEventProducer;
    private final ProjectDeletedEventProducer projectDeletedEventProducer;
    private final ProjectSkillsUpdatedEventProducer projectSkillsUpdatedEventProducer;
    private final ProjectSkillMapper projectSkillMapper;
    private final MediaClient mediaClient;
    private final MediaErrorMapper mediaErrorMapper;
    private final ProjectImageProperties projectImageProperties;
    private final ProjectImageRepository projectImageRepository;

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, UserPrincipal user) {
        if (projectRepository.existsByName(request.getName())) {
            throw new ProjectAlreadyExistsException(ErrorCode.PROJECT_ALREADY_EXISTS);
        }

        Project project = projectMapper.toEntity(request);
        project.setUserId(user.userId());

        projectRepository.save(project);
        log.info("Project with id {} saved in repository", project.getId());

        projectCreatedEventProducer.sendProjectCreated(projectMapper.toProjectCreatedEvent(project));

        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(UpdateProjectRequest request, UUID projectId, UserPrincipal user) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, user.userId());

        projectMapper.updateEntityFromDto(request, project);
        project.setUpdatedAt(LocalDateTime.now());
        log.info("Project with id {} updated successfully", project.getId());

        projectUpdatedEventProducer.sendProjectUpdated(projectMapper.toProjectUpdatedEvent(project));

        return projectMapper.toDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailsResponse getProject(UUID projectId, UserPrincipal user) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, user.userId());

        log.info("Successfully get project with id {}", project.getId());

        return projectMapper.toDetailsDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProjectResponse> getUserProjects(
            UUID userId,
            UserPrincipal user,
            ProjectFilterRequest filter,
            Pageable pageable
    ) {
        Page<Project> projectsPage;

        if (!userId.equals(user.userId())) {
            filter.setProjectPublic(true);
        }

        projectsPage = getUserProjectsWithFilters(userId, filter, pageable);

        log.info("Successfully get user projects with user id {}", userId);

        return projectMapper.toPagedDto(projectsPage);
    }

    @Override
    @Transactional
    public void deleteProject(UUID projectId, UserPrincipal user) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, user.userId());

        projectRepository.delete(project);
        log.info("Successfully delete project with id {}", projectId);

        projectDeletedEventProducer.sendProjectDeleted(new ProjectDeletedEvent(projectId));
    }

    @Override
    @Transactional
    public ProjectSkillResponse addSkillProject(UUID projectId, UUID skillId, UserPrincipal user) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, user.userId());

        return projectSkillService.addForProject(project, skillId, true);
    }

    @Override
    @Transactional
    public void deleteSkillProject(UUID projectId, UUID skillId, UserPrincipal user) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, user.userId());

        projectSkillService.deleteForProject(project, skillId);
    }

    @Override
    @Transactional(readOnly = true)
    public VerificationResponse verifySkillProject(UUID projectId, UserPrincipal user) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, user.userId());

        List<ProjectSkill> projectSkills = project.getSkills();

        System.out.println(projectSkills);

        skillEventProducer.sendVerificationRequest(
                new ProjectSkillVerificationRequestedEvent(
                        projectId, project.getGithubUrl(), skillEventMapper.toEventList(projectSkills))
        );

        return new VerificationResponse("VERIFICATION_REQUESTED");
    }

    @Override
    @Transactional
    public void confirmSkillProject(ProjectSkillVerificationCompletedEvent event) {
        Project project = projectRepository.findById(event.projectId())
                .orElse(null);

        if (project == null) {
            log.warn("Project {} not found, skipping verification result", event.projectId());
            return;
        }

        project.getSkills().forEach(skill -> skill.setConfirmed(false));

        for (SkillVerificationResult s : event.results()) {
            if (s.confirmed()) {
                projectSkillService.confirmProjectSkill(s.projectSkillId());
            }
        }

        List<ProjectSkillDto> skills = projectSkillMapper.toProjectSkillDto(project.getSkills());

        ProjectSkillsUpdatedEvent updatedEvent = new ProjectSkillsUpdatedEvent(
                project.getId(),
                skills
        );

        projectSkillsUpdatedEventProducer.sendProjectSkillsUpdated(updatedEvent);
    }

    @Override
    @Transactional
    public List<ProjectSkillResponse> getProjectSkills(UUID projectId, UserPrincipal user) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, user.userId());

        log.info("Get projects skill with project id {}", projectId);

        return projectSkillService.getProjectSkills(project);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProjectFeedResponse> getProjectsFeed(Pageable pageable) {
        Page<ProjectFeedResponse> page = projectRepository.findFeed(pageable);

        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional
    public MediaResponse uploadPreviewPhoto(UUID projectId,
                                            UserPrincipal currentUser,
                                            MultipartFile file) {

        log.info("Uploading preview photo: projectId={}, userId={}, fileName={}",
                projectId,
                currentUser.userId(),
                file != null ? file.getOriginalFilename() : "null"
        );

        Project project = getProjectOrThrow(projectId);

        checkAccess(project, currentUser.userId());

        MediaResponse response = safeUpload(file, "/project/previews");

        project.setMainImageUrl(response.url());

        return response;
    }

    @Override
    @Transactional
    public MediaResponse uploadProjectPhoto(UUID projectId, UserPrincipal currentUser, MultipartFile file) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, currentUser.userId());

        int count = projectImageRepository.countByProjectId((projectId));

        if (count >= projectImageProperties.getMaxCount()) {
            throw new ProjectTooManyImagesException(ErrorCode.PROJECT_TOO_MANY_IMAGES);
        }

        MediaResponse response = safeUpload(file, "projects/images");

        ProjectImage projectImage = new ProjectImage();
        projectImage.setProject(project);
        projectImage.setImageUrl(response.url());

        projectImageRepository.saveAndFlush(projectImage);

        return response;
    }

    @Override
    @Transactional
    public void deletePreviewPhoto(UUID projectId, UserPrincipal currentUser) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, currentUser.userId());

        if (project.getMainImageUrl() == null) {
            throw new ProjectMainImageNotFoundException(ErrorCode.PROJECT_MAIN_IMAGE_NOT_FOUND);
        }

        safeDelete(project.getMainImageUrl());

        project.setMainImageUrl(null);
    }

    @Override
    @Transactional
    public void deleteProjectPhoto(UUID projectId, UserPrincipal currentUser, String url) {
        Project project = getProjectOrThrow(projectId);

        checkAccess(project, currentUser.userId());

        ProjectImage projectImage = projectImageRepository.findByImageUrl(url)
                .orElseThrow(() -> new ProjectImageNotFoundException(ErrorCode.PROJECT_IMAGE_NOT_FOUND));

        safeDelete(url);

        projectImageRepository.delete(projectImage);
    }

    private Project getProjectOrThrow(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found: projectId={}", projectId);
                    return new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND);
                });
    }

    private void checkAccess(Project project, UUID userId) {
        if (!project.getUserId().equals(userId)) {
            log.warn("Access denied: projectId={}, userId={}",
                    project.getId(), userId);
            throw new ProjectAccessDeniedException(ErrorCode.PROJECT_ACCESS_DENIED);
        }
    }

    private MediaResponse safeUpload(MultipartFile file, String folder) {
        try {
            return mediaClient.upload(file, folder);
        } catch (FeignException ex) {
            log.error("Media upload failed: status={}, body={}",
                    ex.status(), ex.contentUTF8(), ex);
            throw mediaErrorMapper.map(ex);
        }
    }

    private void safeDelete(String url) {
        try {
            mediaClient.delete(url);
        } catch (FeignException ex) {
            log.error("Media delete failed: status={}, body={}",
                    ex.status(), ex.contentUTF8(), ex);
            throw mediaErrorMapper.map(ex);
        }
    }


    private Page<Project> getUserProjectsWithFilters(UUID userId, ProjectFilterRequest filter, Pageable pageable) {
        Specification<Project> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));

            if (filter.getSearch() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getSearch().toLowerCase() + "%"));
            }
            if (filter.getProjectPublic() != null) {
                predicates.add(cb.equal(root.get("projectPublic"), filter.getProjectPublic()));
            }
            if (filter.getCreatedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAfter()));
            }
            if (filter.getCreatedBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return projectRepository.findAll(spec, pageable);
    }
}
