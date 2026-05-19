package korobkin.nikita.project_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectFavoriteResponse(
        UUID favoriteId,
        UUID projectId,
        LocalDateTime createdAt
) {
}
