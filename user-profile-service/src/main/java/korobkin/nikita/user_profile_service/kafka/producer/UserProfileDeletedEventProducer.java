package korobkin.nikita.user_profile_service.kafka.producer;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.user_profile_service.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileDeletedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendUserDeleted(UserDeletedEvent event) {
        kafkaTemplate.send(topics.getUserDeleted(), event);
        log.info("UserDeleted event sent to topic: {} with {}", topics.getUserDeleted(), event);
    }
}
