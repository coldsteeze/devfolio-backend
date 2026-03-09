package korobkin.nikita.project_service.fixtures;

import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.entity.Project;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProjectRequestFixtures {

    public static CreateProjectRequest createProjectRequest(Project project) {
        return CreateProjectRequest.builder()
                .name(project.getName())
                .description(project.getDescription())
                .githubUrl(project.getGithubUrl())
                .projectPublic(project.isProjectPublic())
                .build();
    }

    public static UpdateProjectRequest updateProjectRequest(Project project) {
        return UpdateProjectRequest.builder()
                .name(project.getName())
                .description(project.getDescription())
                .githubUrl(project.getGithubUrl())
                .projectPublic(project.isProjectPublic())
                .build();
    }
}
