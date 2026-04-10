package korobkin.nikita.project_service.kafka.producer;

import korobkin.nikita.events.ProjectSkillAddedEvent;
import korobkin.nikita.project_service.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillAddedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendProjectSkillAdded(ProjectSkillAddedEvent event) {
        kafkaTemplate.send(topics.getProjectSkillAdded(), event);
        log.info(
                "ProjectSkillAdded event sent to topic: {} with {}",
                topics.getProjectSkillAdded(),
                event
        );
    }
}
