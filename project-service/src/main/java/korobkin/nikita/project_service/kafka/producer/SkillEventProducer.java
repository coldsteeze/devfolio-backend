package korobkin.nikita.project_service.kafka.producer;

import korobkin.nikita.events.ProjectSkillVerificationRequestedEvent;
import korobkin.nikita.project_service.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendVerificationRequest(ProjectSkillVerificationRequestedEvent event) {
        kafkaTemplate.send(topics.getProjectSkillVerificationRequested(), event);
        log.info(
                "ProjectSkillVerificationRequested event sent to topic: {} with {}",
                topics.getProjectSkillVerificationRequested(),
                event
        );
    }
}
