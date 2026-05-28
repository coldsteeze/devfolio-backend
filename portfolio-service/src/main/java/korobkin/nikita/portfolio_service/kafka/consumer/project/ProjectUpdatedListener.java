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
        log.info("received ProjectUpdatedEvent: {}", event);

        processedEventService.process(
                event.eventId(),
                () -> portfolioService.updatePortfolioProject(event)
        );
    }
}