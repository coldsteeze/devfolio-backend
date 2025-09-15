package korobkin.nikita.auth_service.kafka.consumer;

import korobkin.nikita.auth_service.service.AuthService;
import korobkin.nikita.events.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDeletedListener {

    private final AuthService authService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userDeleted}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("received UserDeletedEvent: {}", event);
        authService.deleteUser(event);
    }
}
