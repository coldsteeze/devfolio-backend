package korobkin.nikita.project_service.service;

import java.util.UUID;

public interface OutboxEventService {

    void saveEvent(String aggregateType, UUID aggregateId, String eventType, Object payload);
}
