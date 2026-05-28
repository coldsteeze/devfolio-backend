package korobkin.nikita.events;

import java.util.UUID;

public record UserDeletedEvent(
        UUID eventId,
        UUID userId
) { }
