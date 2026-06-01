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

        log.info("Received UserCreatedEvent eventId={}, userId={}",
                event.eventId(), event.userId());

        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Duplicate UserCreatedEvent ignored eventId={}", event.eventId());
            return;
        }

        try {
            userProfileService.createUserEmptyProfile(event);

            ProcessedEvent processedEvent =
                    ProcessedEvent.builder()
                            .eventId(event.eventId())
                            .processedAt(Instant.now())
                            .build();

            processedEventRepository.save(processedEvent);

            log.info("UserCreatedEvent processed successfully eventId={}", event.eventId());

        } catch (Exception ex) {
            log.error("Failed to process UserCreatedEvent eventId={}", event.eventId(), ex);
            throw ex;
        }
    }
}

