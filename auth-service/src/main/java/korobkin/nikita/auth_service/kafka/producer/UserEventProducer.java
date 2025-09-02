package korobkin.nikita.auth_service.kafka.producer;

import korobkin.nikita.auth_service.config.KafkaTopicProperties;
import korobkin.nikita.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendUserCreated(UserCreatedEvent event) {
        kafkaTemplate.send(topics.getUserCreated(), event);
        log.info("UserCreated event sent to topic: {} with {}", topics.getUserCreated(), event);
    }
}
