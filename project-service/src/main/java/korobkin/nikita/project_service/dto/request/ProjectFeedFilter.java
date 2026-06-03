package korobkin.nikita.project_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.events.skill.SkillCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filters for project feed")
public class ProjectFeedFilter {

    @Schema(
            description = "List of skill IDs used for filtering projects by skills",
            example = "[\"550e8400-e29b-41d4-a716-446655440000\", \"550e8400-e29b-41d4-a716-446655440001\"]"
    )
    private List<UUID> skillIds;

    @Schema(
            description = "List of skill categories used for filtering projects",
            example = "[\"LANGUAGE\", \"FRAMEWORK\"]"
    )
    private List<SkillCategory> categories;
}
