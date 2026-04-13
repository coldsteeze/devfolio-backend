package korobkin.nikita.project_service.kafka.producer;

import korobkin.nikita.events.ProjectSkillsUpdatedEvent;
import korobkin.nikita.project_service.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillsUpdatedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendProjectSkillsUpdated(ProjectSkillsUpdatedEvent event) {
        kafkaTemplate.send(topics.getProjectSkillsUpdated(), event);
        log.info("ProjectSkillsUpdatedEvent sent: {}", event);
    }
}
