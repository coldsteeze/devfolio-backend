package korobkin.nikita.project_service.fixtures;

import korobkin.nikita.project_service.entity.ProjectLike;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ProjectLikeFixtures {

    public static ProjectLike projectLike(UUID userId, UUID projectId) {
        ProjectLike projectLike = new ProjectLike();
        projectLike.setUserId(userId);
        projectLike.setProjectId(projectId);

        return projectLike;
    }
}
