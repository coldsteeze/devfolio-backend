package korobkin.nikita.auth_service.kafka.consumer;

import korobkin.nikita.auth_service.entity.ProcessedEvent;
import korobkin.nikita.auth_service.repository.ProcessedEventRepository;
import korobkin.nikita.auth_service.service.AuthService;
import korobkin.nikita.events.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDeletedListener {

    private final AuthService authService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userDeleted}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserDeleted(UserDeletedEvent event) {

        log.info("Received UserDeletedEvent eventId={}, userId={}",
                event.eventId(), event.userId());

        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Duplicate UserDeletedEvent ignored eventId={}", event.eventId());
            return;
        }

        try {
            authService.deleteUser(event);

            processedEventRepository.save(
                    ProcessedEvent.builder()
                            .eventId(event.eventId())
                            .processedAt(Instant.now())
                            .build()
            );

            log.info("UserDeletedEvent processed successfully eventId={}", event.eventId());

        } catch (Exception ex) {
            log.error("Failed to process UserDeletedEvent eventId={}", event.eventId(), ex);
            throw ex;
        }
    }
}
