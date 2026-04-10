package korobkin.nikita.project_service.kafka.producer;

import korobkin.nikita.events.ProjectUpdatedEvent;
import korobkin.nikita.project_service.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectUpdatedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendProjectUpdated(ProjectUpdatedEvent event) {
        kafkaTemplate.send(topics.getProjectUpdated(), event);
        log.info(
                "ProjectUpdated event sent to topic: {} with {}",
                topics.getProjectUpdated(),
                event
        );
    }
}
