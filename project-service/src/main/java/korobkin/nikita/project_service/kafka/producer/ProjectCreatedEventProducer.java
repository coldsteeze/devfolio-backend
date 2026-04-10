package korobkin.nikita.project_service.kafka.producer;

import korobkin.nikita.events.ProjectCreatedEvent;
import korobkin.nikita.project_service.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectCreatedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendProjectCreated(ProjectCreatedEvent event) {
        kafkaTemplate.send(topics.getProjectCreated(), event);
        log.info(
                "ProjectCreated event sent to topic: {} with {}",
                topics.getProjectCreated(),
                event
        );
    }
}
