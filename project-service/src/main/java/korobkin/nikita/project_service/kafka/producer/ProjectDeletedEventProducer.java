package korobkin.nikita.project_service.kafka.producer;

import korobkin.nikita.events.ProjectDeletedEvent;
import korobkin.nikita.project_service.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectDeletedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendProjectDeleted(ProjectDeletedEvent event) {
        kafkaTemplate.send(topics.getProjectDeleted(), event);
        log.info(
                "ProjectDeleted event sent to topic: {} with {}",
                topics.getProjectDeleted(),
                event
        );
    }
}
