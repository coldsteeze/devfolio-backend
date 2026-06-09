package korobkin.nikita.events;

import java.util.UUID;

public record CareerEntryPayload(

        UUID id,

        CareerEntryType type,

        String title,

        String organization,

        String description,

        CareerDatePayload startDate,

        CareerDatePayload endDate
) {}
