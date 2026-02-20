package korobkin.nikita.skill_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import korobkin.nikita.skill_service.entity.enums.SkillCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request dto for update skill")
public class UpdateSkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(min = 2, max = 50, message = "Skill name must be between 2 and 50 characters")
    @Schema(example = "Java", description = "Field for skill name")
    private String name;

    @Schema(
            example = "LANGUAGE",
            description = "Type of skill (e.g., FRAMEWORK, TOOL, PLATFORM)"
    )
    private SkillCategory category;
}
