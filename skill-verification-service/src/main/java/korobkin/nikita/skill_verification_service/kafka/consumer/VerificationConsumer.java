package korobkin.nikita.skill_verification_service.kafka.consumer;

import korobkin.nikita.events.ProjectSkillVerificationRequestedEvent;
import korobkin.nikita.skill_verification_service.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationConsumer {

    private final VerificationService service;

    @KafkaListener(topics = "#{@kafkaTopicProperties.projectSkillVerificationRequested}")
    public void handle(ProjectSkillVerificationRequestedEvent event) {
        service.verify(event);
    }
}
