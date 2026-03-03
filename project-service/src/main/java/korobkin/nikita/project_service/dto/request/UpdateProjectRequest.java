package korobkin.nikita.project_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
public class UpdateProjectRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description must be up to 1000 characters long")
    private String description;

    @URL(message = "Github URL must be a valid URL")
    private String githubUrl;

    private boolean projectPublic;
}
