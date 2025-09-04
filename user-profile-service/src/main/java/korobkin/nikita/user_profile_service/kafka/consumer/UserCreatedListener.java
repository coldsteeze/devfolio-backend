package korobkin.nikita.user_profile_service.kafka.consumer;

import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedListener {

    private final UserProfileService userProfileService;

    @KafkaListener(
            topics = "${app.kafka.topics.user-created}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("received UserCreatedEvent: {}", event);
        userProfileService.createUserProfile(event);
    }
}

