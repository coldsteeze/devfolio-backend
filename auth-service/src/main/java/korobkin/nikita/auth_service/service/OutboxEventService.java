package korobkin.nikita.auth_service.service;

import korobkin.nikita.events.UserCreatedEvent;

import java.util.UUID;

public interface OutboxEventService {

    void saveEvent(String aggregateType, UUID aggregateId, String eventType, UserCreatedEvent payload);
}
