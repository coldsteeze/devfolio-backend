package korobkin.nikita.project_service.dto.response;

import java.util.UUID;

public record ProjectSkillResponse(
        UUID skillId,
        boolean confirmed,
        boolean manuallyAdded
) {
}
