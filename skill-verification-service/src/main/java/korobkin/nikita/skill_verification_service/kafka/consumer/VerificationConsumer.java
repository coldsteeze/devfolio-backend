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
        log.info("Received verification request: projectId={}, skills={}",
                event.projectId(), event.skills().size());

        if (processedEventRepository.existsById(event.eventId())) {

            log.info(
                    "event already processed: {}",
                    event.eventId()
            );

            return;
        }

        service.verify(event);

        ProcessedEvent processedEvent =
                ProcessedEvent.builder()
                        .eventId(event.eventId())
                        .processedAt(Instant.now())
                        .build();

        processedEventRepository.save(processedEvent);

        log.info(
                "event processed successfully: {}",
                event.eventId()
        );
    }
}
