package korobkin.nikita.project_service.service;

import korobkin.nikita.project_service.dto.response.LikeStatusResponse;
import korobkin.nikita.project_service.security.user.UserPrincipal;

import java.util.UUID;

public interface ProjectInteractionService {

    void recordView(UUID projectId, UserPrincipal currentUser);

    void like(UUID projectId, UserPrincipal currentUser);

    void removeLike(UUID projectId, UserPrincipal currentUser);

    LikeStatusResponse isLiked(UUID projectId, UserPrincipal currentUser);
}
