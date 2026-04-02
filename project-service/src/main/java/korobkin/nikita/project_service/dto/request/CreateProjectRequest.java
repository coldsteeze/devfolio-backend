package korobkin.nikita.project_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new project")
public class CreateProjectRequest {

    @Schema(example = "Portfolio Platform", description = "Project name")
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Schema(example = "A platform for showcasing developer portfolios", description = "Project description")
    @Size(max = 1000, message = "Description must be up to 1000 characters long")
    private String description;

    @Schema(example = "https://github.com/user/project", description = "GitHub repository URL")
    @URL(message = "Github URL must be a valid URL")
    private String githubUrl;

    @Schema(example = "true", description = "Project visibility (true = public, false = private)")
    private boolean projectPublic;
}