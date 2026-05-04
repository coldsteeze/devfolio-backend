package korobkin.nikita.project_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response DTO containing detailed project information with skills")
public record ProjectDetailsResponse(

        @Schema(description = "Project basic information")
        ProjectResponse project,

        @Schema(description = "List of project skills")
        List<ProjectSkillResponse> skills,

        @Schema(description = "List of project images")
        List<ProjectImageResponse> images
) {
}
