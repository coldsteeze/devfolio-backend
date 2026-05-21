package korobkin.nikita.project_service.service.impl;

import korobkin.nikita.project_service.dto.response.LikeStatusResponse;
import korobkin.nikita.project_service.entity.ProjectLike;
import korobkin.nikita.project_service.entity.ProjectView;
import korobkin.nikita.project_service.exception.ErrorCode;
import korobkin.nikita.project_service.exception.ProjectNotFoundException;
import korobkin.nikita.project_service.repository.ProjectLikeRepository;
import korobkin.nikita.project_service.repository.ProjectRepository;
import korobkin.nikita.project_service.repository.ProjectViewRepository;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import korobkin.nikita.project_service.service.ProjectInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectInteractionServiceImpl implements ProjectInteractionService {

    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectViewRepository projectViewRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public void recordView(UUID projectId, UserPrincipal currentUser) {
        log.debug("Entering recordView: projectId={}, userId={}", projectId, currentUser.userId());

        UUID ownerId = projectRepository.findOwnerIdByProjectId(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found for view recording: projectId={}", projectId);
                    return new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND);
                });

        if (ownerId.equals(currentUser.userId())) {
            log.debug("View ignored: viewer is project owner. projectId={}, ownerId={}",
                    projectId, ownerId);
            return;
        }

        ProjectView projectView = new ProjectView();
        projectView.setProjectId(projectId);
        projectView.setUserId(currentUser.userId());

        projectViewRepository.save(projectView);
        log.debug("ProjectView entity saved: viewId={}, projectId={}, userId={}",
                projectView.getId(), projectId, currentUser.userId());

        projectRepository.incrementViews(projectId);
        log.info("View recorded successfully: projectId={}, userId={}",
                projectId, currentUser.userId());
        log.debug("Exiting recordView: projectId={}, userId={}", projectId, currentUser.userId());
    }

    @Override
    @Transactional
    public void like(UUID projectId, UserPrincipal currentUser) {
        log.debug("Entering like: projectId={}, userId={}", projectId, currentUser.userId());

        if (projectLikeRepository.existsByProjectIdAndUserId(projectId, currentUser.userId())) {
            log.debug("Idempotent like creation skipped: already exists. projectId={}, userId={}",
                    projectId, currentUser.userId());
            return;
        }

        try {
            ProjectLike projectLike = new ProjectLike();
            projectLike.setUserId(currentUser.userId());
            projectLike.setProjectId(projectId);

            projectLikeRepository.save(projectLike);
            log.debug("ProjectLike entity saved: projectId={}, userId={}",
                    projectId, currentUser.userId());

            projectRepository.incrementLikes(projectId);
            log.info("Like added successfully: projectId={}, userId={}",
                    projectId, currentUser.userId());
        } catch (DataIntegrityViolationException e) {
            log.debug("Race condition on like creation (ignored): projectId={}, userId={}, error={}",
                    projectId, currentUser.userId(), e.getMessage());
        }

        log.debug("Exiting like: projectId={}, userId={}", projectId, currentUser.userId());
    }

    @Override
    @Transactional
    public void removeLike(UUID projectId, UserPrincipal currentUser) {
        log.debug("Entering removeLike: projectId={}, userId={}", projectId, currentUser.userId());

        int deletedCount = projectLikeRepository.deleteByProjectIdAndUserId(
                projectId,
                currentUser.userId()
        );
        log.debug("Delete operation result: deletedCount={}, projectId={}, userId={}",
                deletedCount, projectId, currentUser.userId());

        if (deletedCount > 0) {
            projectRepository.decrementLikes(projectId);
            log.info("Like removed successfully: projectId={}, userId={}",
                    projectId, currentUser.userId());
        } else {
            log.debug("Like not found, nothing to remove (idempotent): projectId={}, userId={}",
                    projectId, currentUser.userId());
        }

        log.debug("Exiting removeLike: projectId={}, userId={}", projectId, currentUser.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public LikeStatusResponse isLiked(UUID projectId, UserPrincipal currentUser) {
        log.debug("Entering isLiked: projectId={}, userId={}", projectId, currentUser.userId());

        boolean liked = projectLikeRepository.existsByProjectIdAndUserId(
                projectId,
                currentUser.userId()
        );

        log.debug("isLiked result: projectId={}, userId={}, liked={}",
                projectId, currentUser.userId(), liked);

        return new LikeStatusResponse(liked);
    }
}