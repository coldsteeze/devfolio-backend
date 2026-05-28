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
        log.info("received UserDeletedEvent: {}", event);

        if (processedEventRepository.existsById(event.eventId())) {

            log.info(
                    "event already processed: {}",
                    event.eventId()
            );

            return;
        }

        authService.deleteUser(event);

        ProcessedEvent processedEvent =
                ProcessedEvent.builder()
                        .eventId(event.eventId())
                        .processedAt(Instant.now())
                        .build();

        processedEventRepository.save(processedEvent);

        log.info(
                "event processed successfully: {}",
                event.eventId()
        );
    }
}
