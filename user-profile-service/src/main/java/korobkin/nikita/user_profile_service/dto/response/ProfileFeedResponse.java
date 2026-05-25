package korobkin.nikita.user_profile_service.dto.response;

import korobkin.nikita.user_profile_service.entity.enums.UserType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileFeedResponse(
        UUID userId,
        String nickname,
        String displayName,
        String avatarUrl,
        UserType userType,
        String bioSnippet,
        LocalDateTime createdAt
) {
}
