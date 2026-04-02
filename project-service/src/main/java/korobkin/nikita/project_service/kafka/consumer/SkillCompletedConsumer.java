package korobkin.nikita.project_service.kafka.consumer;

import korobkin.nikita.events.ProjectSkillVerificationCompletedEvent;
import korobkin.nikita.project_service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillCompletedConsumer {

    private final ProjectService projectService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectSkillVerificationCompleted}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSkillVerificationCompleted(ProjectSkillVerificationCompletedEvent event) {
        log.info("received ProjectSkillVerificationCompletedEvent: {}", event);
        projectService.confirmSkillProject(event);
    }
}
