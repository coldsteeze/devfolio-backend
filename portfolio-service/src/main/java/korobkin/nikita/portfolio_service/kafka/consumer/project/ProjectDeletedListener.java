package korobkin.nikita.portfolio_service.kafka.consumer.project;

import korobkin.nikita.events.ProjectDeletedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectDeletedListener {

    private final PortfolioService portfolioService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectDeleted}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleProjectDeleted(ProjectDeletedEvent event) {
        log.info("received ProjectDeletedEvent: {}", event);
        portfolioService.deletePortfolioProject(event);
    }
}
