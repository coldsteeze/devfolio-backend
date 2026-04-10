package korobkin.nikita.project_service.kafka.producer;

import korobkin.nikita.events.ProjectSkillRemovedEvent;
import korobkin.nikita.project_service.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillRemovedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendProjectSkillRemoved(ProjectSkillRemovedEvent event) {
        kafkaTemplate.send(topics.getProjectSkillRemoved(), event);
        log.info(
                "ProjectSkillRemoved event sent to topic: {} with {}",
                topics.getProjectSkillRemoved(),
                event
        );
    }
}
