package korobkin.nikita.skill_verification_service.kafka.consumer;

import korobkin.nikita.events.ProjectSkillVerificationRequestedEvent;
import korobkin.nikita.skill_verification_service.entity.ProcessedEvent;
import korobkin.nikita.skill_verification_service.repository.ProcessedEventRepository;
import korobkin.nikita.skill_verification_service.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationConsumer {

    private final VerificationService service;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "#{@kafkaTopicProperties.projectSkillVerificationRequested}")
    public void handle(ProjectSkillVerificationRequestedEvent event) {

        log.info("Received VerificationRequestedEvent: projectId={}, skills={}",
                event.projectId(), event.skills().size());

        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Duplicate VerificationRequestedEvent ignored eventId={}", event.eventId());
            return;
        }

        try {
            service.verify(event);

            ProcessedEvent processedEvent =
                    ProcessedEvent.builder()
                            .eventId(event.eventId())
                            .processedAt(Instant.now())
                            .build();

            processedEventRepository.save(processedEvent);

            log.info("VerificationRequestedEvent processed successfully eventId={}", event.eventId());
        }  catch (Exception ex) {
            log.error("Failed to process VerificationRequestedEvent eventId={}", event.eventId(), ex);
            throw ex;
        }
    }
}
