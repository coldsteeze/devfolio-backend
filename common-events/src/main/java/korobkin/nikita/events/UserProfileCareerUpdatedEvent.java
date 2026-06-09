package korobkin.nikita.events;

import java.util.List;
import java.util.UUID;

public record UserProfileCareerUpdatedEvent(

        UUID eventId,

        UUID userId,

        List<CareerEntryPayload> career

) {}
