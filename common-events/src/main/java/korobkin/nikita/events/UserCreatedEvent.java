package korobkin.nikita.events;

import java.util.UUID;

public record UserCreatedEvent(
        UUID userId
) {}
