package korobkin.nikita.events;

import java.util.UUID;

public record ProjectDeletedEvent(
        UUID projectId
) {
}
