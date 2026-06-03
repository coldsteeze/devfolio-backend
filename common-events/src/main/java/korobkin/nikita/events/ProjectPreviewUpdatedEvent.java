package korobkin.nikita.events;

import java.util.UUID;

public record ProjectPreviewUpdatedEvent(
        UUID eventId,
        UUID projectId,
        String mainImageUrl
) {
}
