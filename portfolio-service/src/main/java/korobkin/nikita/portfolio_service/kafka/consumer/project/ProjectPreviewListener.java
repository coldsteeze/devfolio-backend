package korobkin.nikita.portfolio_service.kafka.consumer.project;

import korobkin.nikita.events.ProjectPreviewUpdatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioProjectService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectPreviewListener {

    private final PortfolioProjectService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectPreviewUpdated}",
            containerFactory = "projectPreviewUpdatedKafkaListenerContainerFactory"
    )
    public void handleProjectPreviewUpdated(ProjectPreviewUpdatedEvent event) {

        log.info(
                "Received ProjectPreviewUpdatedEvent eventId={}, projectId={}",
                event.eventId(),
                event.projectId()
        );

        try {
            processedEventService.process(
                    event.eventId(),
                    () -> portfolioService.updatePortfolioProjectPreview(event)
            );

            log.info(
                    "ProjectPreviewUpdatedEvent processed eventId={}",
                    event.eventId()
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to process ProjectPreviewUpdatedEvent eventId={}",
                    event.eventId(),
                    ex
            );

            throw ex;
        }
    }
}
