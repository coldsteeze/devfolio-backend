package korobkin.nikita.project_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        String githubUrl,
        boolean projectPublic,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
