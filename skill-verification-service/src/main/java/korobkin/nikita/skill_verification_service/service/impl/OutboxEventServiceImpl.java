package korobkin.nikita.skill_verification_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import korobkin.nikita.skill_verification_service.entity.OutboxEvent;
import korobkin.nikita.skill_verification_service.repository.OutboxEventRepository;
import korobkin.nikita.skill_verification_service.service.OutboxEventService;
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
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void saveEvent(String aggregateType,
                          UUID aggregateId,
                          String eventType,
                          Object payload) {

        log.debug("Creating outbox event aggregateType={}, aggregateId={}, eventType={}",
                aggregateType, aggregateId, eventType);

        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(payload))
                    .createdAt(Instant.now())
                    .build();

            OutboxEvent saved = outboxEventRepository.save(outboxEvent);

            log.info("Outbox event saved id={}, aggregateId={}, eventType={}",
                    saved.getId(), aggregateId, eventType);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox event aggregateType={}, aggregateId={}, eventType={}",
                    aggregateType, aggregateId, eventType, e);
            throw new RuntimeException("Failed to serialize outbox event", e);
        }
    }
}