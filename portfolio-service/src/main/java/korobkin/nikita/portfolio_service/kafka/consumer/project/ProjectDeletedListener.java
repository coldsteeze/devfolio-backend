package korobkin.nikita.portfolio_service.kafka.consumer.project;

import korobkin.nikita.events.ProjectDeletedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioProjectService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectDeletedListener {

    private final PortfolioProjectService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectDeleted}",
            containerFactory = "projectDeletedKafkaListenerContainerFactory"
    )
    public void handleProjectDeleted(ProjectDeletedEvent event) {

        log.info("Received ProjectDeletedEvent eventId={}, projectId={}",
                event.eventId(),
                event.projectId()
        );

        try {
            processedEventService.process(
                    event.eventId(),
                    () -> portfolioService.deletePortfolioProject(event)
            );

            log.info(
                    "ProjectDeletedEvent processed eventId={}",
                    event.eventId()
            );
        } catch(Exception ex) {
            log.error(
                    "Failed to process ProjectDeletedEvent eventId={}",
                    event.eventId(),
                    ex
            );

            throw ex;
        }
    }
}
