package korobkin.nikita.user_profile_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.user_profile_service.entity.enums.UserType;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Brief user profile information for display in the user feed")
public record ProfileFeedResponse(

        @Schema(description = "Unique identifier of the user", format = "uuid", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "Unique username/nickname", example = "dev_master")
        String nickname,

        @Schema(description = "Full display name (combined first and last name)", example = "John Doe")
        String displayName,

        @Schema(description = "URL to the user's profile avatar", format = "uri", example = "https://cdn.example.com/avatars/avatar_123.jpg")
        String avatarUrl,

        @Schema(description = "Role or category of the user", example = "JOB_SEEKER")
        UserType userType,

        @Schema(description = "Truncated biography text for feed preview (max ~150 characters)", example = "Passionate about building scalable backend systems...")
        String bioSnippet,

        @Schema(description = "Timestamp when the profile was created", format = "date-time", example = "2026-05-20T10:30:00")
        LocalDateTime createdAt
) {
}