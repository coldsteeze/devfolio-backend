package korobkin.nikita.project_service.dto.response;

import java.util.List;

public record ProjectDetailsResponse(
        ProjectResponse project,
        List<ProjectSkillResponse> skills
) {
}
