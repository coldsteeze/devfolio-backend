package korobkin.nikita.portfolio_service.fixtures;

import korobkin.nikita.events.ProjectCreatedEvent;
import korobkin.nikita.events.ProjectDeletedEvent;
import korobkin.nikita.events.ProjectUpdatedEvent;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ProjectEventFixtures {

    public static ProjectCreatedEvent create(UUID projectId, UUID userId, boolean isPublic) {
        return new ProjectCreatedEvent(
                projectId,
                userId,
                "proj",
                "desc",
                "git",
                isPublic
        );
    }

    public static ProjectUpdatedEvent update(UUID projectId, UUID userId, boolean isPublic) {
        return new ProjectUpdatedEvent(
                projectId,
                userId,
                "newName",
                "newDesc",
                "newGit",
                isPublic
        );
    }

    public static ProjectDeletedEvent delete(UUID projectId) {
        return new ProjectDeletedEvent(projectId);
    }
}