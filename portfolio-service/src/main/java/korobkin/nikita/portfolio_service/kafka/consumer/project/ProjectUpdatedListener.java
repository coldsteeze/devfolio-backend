package korobkin.nikita.portfolio_service.kafka.consumer.project;

import korobkin.nikita.events.ProjectUpdatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioProjectService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectUpdatedListener {

    private final PortfolioProjectService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectUpdated}",
            containerFactory = "projectUpdatedKafkaListenerContainerFactory"
    )
    public void handleProjectUpdated(ProjectUpdatedEvent event) {

        log.info("Received ProjectUpdatedEvent eventId={}, projectId={}",
                event.eventId(),
                event.projectId()
        );

        try {
            processedEventService.process(
                    event.eventId(),
                    () -> portfolioService.updatePortfolioProject(event)
            );

            log.info(
                    "ProjectUpdatedEvent processed eventId={}",
                    event.eventId()
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to process ProjectUpdatedEvent eventId={}",
                    event.eventId(),
                    ex
            );

            throw ex;
        }
    }
}