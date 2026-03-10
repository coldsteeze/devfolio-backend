package korobkin.nikita.project_service.fixtures;

import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.ProjectFilterRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.entity.Project;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

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

    public static ProjectFilterRequest projectFilterRequest(
            String search,
            boolean projectPublic,
            LocalDateTime createdAfter,
            LocalDateTime createdBefore) {
        return ProjectFilterRequest.builder()
                .search(search)
                .projectPublic(projectPublic)
                .createdAfter(createdAfter)
                .createdBefore(createdBefore)
                .build();
    }
}
