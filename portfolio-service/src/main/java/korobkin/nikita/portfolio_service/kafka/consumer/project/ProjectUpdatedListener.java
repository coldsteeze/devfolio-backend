package korobkin.nikita.portfolio_service.kafka.consumer.project;

import korobkin.nikita.events.ProjectUpdatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectUpdatedListener {

    private final PortfolioService portfolioService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectUpdated}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleProjectUpdated(ProjectUpdatedEvent event) {
        log.info("received ProjectUpdatedEvent: {}", event);
        portfolioService.updatePortfolioProject(event);
    }
}