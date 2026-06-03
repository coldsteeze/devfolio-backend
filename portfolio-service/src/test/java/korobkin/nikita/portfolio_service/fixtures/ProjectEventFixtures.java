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
                UUID.randomUUID(),
                projectId,
                userId,
                "proj",
                "short desc",
                "desc",
                "git",
                isPublic
        );
    }

    public static ProjectUpdatedEvent update(UUID projectId, UUID userId, boolean isPublic) {
        return new ProjectUpdatedEvent(
                UUID.randomUUID(),
                projectId,
                userId,
                "newName",
                "short desc",
                "desc",
                "newGit",
                "mainImageUrl",
                isPublic
        );
    }

    public static ProjectDeletedEvent delete(UUID projectId) {
        return new ProjectDeletedEvent(UUID.randomUUID(), projectId);
    }
}