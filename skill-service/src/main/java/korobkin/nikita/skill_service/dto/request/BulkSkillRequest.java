package korobkin.nikita.skill_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Request DTO for search multiple skills")
public class BulkSkillRequest {

    @Size(min = 1, max = 10, message = "Must provide between 1 and 10 skill IDs")
    @NotEmpty(message = "Skill IDs cannot be empty")
    @Schema(
            description = "List of skills to retrieve",
            example = "[\"550e8400-e29b-41d4-a716-446655440000\", \"660e8400-e29b-41d4-a716-446655440001\"]"
    )
    private List<@NotBlank(message = "Skill ID cannot be blank")
    @Pattern(
            regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
            message = "Invalid UUID format"
    )
            String> skillIds;
}
