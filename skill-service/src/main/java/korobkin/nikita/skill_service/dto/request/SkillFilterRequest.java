package korobkin.nikita.skill_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.skill_service.entity.enums.SkillCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request filter DTO for search skills")
public class SkillFilterRequest {

    @Schema(example = "jav", description = "Partial search field")
    private String search;

    @Schema(
            example = "LANGUAGE",
            description = "Type of skill (e.g., FRAMEWORK, TOOL, PLATFORM)"
    )
    private SkillCategory category;

    @Schema(description = "Include inactive skills in search results", defaultValue = "false")
    private boolean includeInactive = false;
}
