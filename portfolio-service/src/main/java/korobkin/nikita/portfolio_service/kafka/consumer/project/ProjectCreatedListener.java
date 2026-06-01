package korobkin.nikita.portfolio_service.kafka.consumer.project;

import korobkin.nikita.events.ProjectCreatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioProjectService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectCreatedListener {

    private final PortfolioProjectService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectCreated}",
            containerFactory = "projectCreatedKafkaListenerContainerFactory"
    )
    public void handleProjectCreated(ProjectCreatedEvent event) {

        log.info(
                "Received ProjectCreatedEvent eventId={}, projectId={}",
                event.eventId(),
                event.projectId()
        );

        try {
            processedEventService.process(
                    event.eventId(),
                    () -> portfolioService.createPortfolioProject(event)
            );

            log.info(
                    "ProjectCreatedEvent processed eventId={}",
                    event.eventId()
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to process ProjectCreatedEvent eventId={}",
                    event.eventId(),
                    ex
            );

            throw ex;
        }
    }
}
