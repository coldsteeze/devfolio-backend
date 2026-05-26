package korobkin.nikita.auth_service.service.impl;

import korobkin.nikita.auth_service.entity.OutboxEvent;
import korobkin.nikita.auth_service.repository.OutboxEventRepository;
import korobkin.nikita.auth_service.service.OutboxEventService;
import korobkin.nikita.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventServiceImpl implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    @Transactional
    public void saveEvent(String aggregateType, UUID aggregateId, String eventType, UserCreatedEvent payload) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .createdAt(Instant.now())
                .build();

        outboxEventRepository.save(outboxEvent);
    }
}
