package korobkin.nikita.project_service.fixtures;

import korobkin.nikita.project_service.entity.Project;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ProjectFixtures {

    public static final String VALID_NAME = "Project 1";
    public static final String DESCRIPTION = "Description";
    public static final String VALID_GITHUB_URL = "https://github.com/project1";
    public static final boolean PROJECT_PUBLIC_TRUE = true;
    public static final boolean PROJECT_PUBLIC_FALSE = false;

    public static final String UPDATED_VALID_NAME = "Updated Project 1";

    public static Project validProject(UUID userId) {
        return project(userId, VALID_NAME, DESCRIPTION, VALID_GITHUB_URL, PROJECT_PUBLIC_TRUE);
    }

    public static Project projectWithProjectPublicFalse(UUID userId) {
        return project(userId, VALID_NAME, DESCRIPTION, VALID_GITHUB_URL, PROJECT_PUBLIC_FALSE);
    }

    public static Project updatedValidProject(UUID userId) {
        return project(userId, UPDATED_VALID_NAME, DESCRIPTION, VALID_GITHUB_URL, PROJECT_PUBLIC_TRUE);
    }

    public static Project projectWithInvalidName(String invalidName) {
        return project(UUID.randomUUID(), invalidName, DESCRIPTION, VALID_GITHUB_URL, PROJECT_PUBLIC_TRUE);
    }

    public static Project projectWithCustomName(UUID userId, String name) {
        return project(userId, name, DESCRIPTION, VALID_GITHUB_URL, PROJECT_PUBLIC_TRUE);
    }

    public static Project project(UUID userId, String name, String description, String githubUrl, boolean projectPublic) {
        Project project = new Project();
        project.setUserId(userId);
        project.setName(name);
        project.setDescription(description);
        project.setGithubUrl(githubUrl);
        project.setProjectPublic(projectPublic);
        project.setLikesCount(0L);
        project.setViewsCount(0L);

        return project;
    }
}
