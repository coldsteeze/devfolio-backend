package korobkin.nikita.project_service.fixtures;

import korobkin.nikita.project_service.entity.ProjectFavorite;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ProjectFavoriteFixtures {

    public static ProjectFavorite projectFavorite(UUID userId, UUID projectId) {
        ProjectFavorite projectFavorite = new ProjectFavorite();
        projectFavorite.setProjectId(projectId);
        projectFavorite.setUserId(userId);

        return projectFavorite;
    }
}
