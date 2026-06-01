package korobkin.nikita.portfolio_service.kafka.consumer.skill;

import korobkin.nikita.events.ProjectSkillsUpdatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioProjectSkillService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillUpdatedListener {

    private final PortfolioProjectSkillService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectSkillsUpdated}",
            containerFactory = "projectSkillsUpdatedKafkaListenerContainerFactory"
    )
    public void handleProjectSkillsUpdated(ProjectSkillsUpdatedEvent event) {

        log.info("Received ProjectSkillsUpdatedEvent eventId={}, projectId={}",
                event.eventId(),
                event.projectId()
        );

        try {
            processedEventService.process(
                    event.eventId(),
                    () -> portfolioService.updatePortfolioProjectSkill(event)
            );

            log.info(
                    "ProjectSkillsUpdatedEvent processed eventId={}",
                    event.eventId()
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to process ProjectSkillsUpdatedEvent eventId={}",
                    event.eventId(),
                    ex
            );

            throw ex;
        }
    }
}
