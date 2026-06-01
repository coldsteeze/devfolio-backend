package korobkin.nikita.project_service.kafka.consumer;

import korobkin.nikita.events.ProjectSkillVerificationCompletedEvent;
import korobkin.nikita.project_service.entity.ProcessedEvent;
import korobkin.nikita.project_service.repository.ProcessedEventRepository;
import korobkin.nikita.project_service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillCompletedConsumer {

    private final ProjectService projectService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectSkillVerificationCompleted}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSkillVerificationCompleted(ProjectSkillVerificationCompletedEvent event) {

        log.info("Received ProjectSkillVerificationCompletedEvent: {}", event);

        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Duplicate ProjectSkillVerificationCompletedEvent ignored eventId={}", event.eventId());
            return;
        }

        try {
            projectService.confirmSkillProject(event);

            ProcessedEvent processedEvent =
                    ProcessedEvent.builder()
                            .eventId(event.eventId())
                            .processedAt(Instant.now())
                            .build();

            processedEventRepository.save(processedEvent);

            log.info("ProjectSkillVerificationCompletedEvent processed successfully eventId={}", event.eventId());
        } catch (Exception ex) {
            log.error("Failed to process ProjectSkillVerificationCompletedEvent eventId={}", event.eventId(), ex);
            throw ex;
        }
    }
}
