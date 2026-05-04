package korobkin.nikita.project_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for updating an existing project")
public class UpdateProjectRequest {

    @Schema(example = "Updated Portfolio Platform", description = "Updated project name")
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Schema(example = "Updated description of the project", description = "Updated project description")
    @Size(max = 1000, message = "Description must be up to 1000 characters long")
    private String description;

    @Schema(example = "Updated short description of the project", description = "Updated short project description")
    @Size(max = 256, message = "Short description must be up to 256 characters long")
    private String shortDescription;

    @Schema(example = "https://github.com/user/updated-project", description = "Updated GitHub repository URL")
    @URL(message = "Github URL must be a valid URL")
    private String githubUrl;

    @Schema(example = "false", description = "Updated project visibility (true = public, false = private)")
    private boolean projectPublic;
}
