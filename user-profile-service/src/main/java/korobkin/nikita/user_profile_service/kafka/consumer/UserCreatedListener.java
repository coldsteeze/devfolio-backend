package korobkin.nikita.user_profile_service.kafka.consumer;

import jakarta.transaction.Transactional;
import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.user_profile_service.entity.ProcessedEvent;
import korobkin.nikita.user_profile_service.repository.ProcessedEventRepository;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedListener {

    private final UserProfileService userProfileService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userCreated}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("received UserCreatedEvent: {}", event);

        if (processedEventRepository.existsById(event.eventId())) {

            log.info(
                    "event already processed: {}",
                    event.eventId()
            );

            return;
        }

        userProfileService.createUserEmptyProfile(event);

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

