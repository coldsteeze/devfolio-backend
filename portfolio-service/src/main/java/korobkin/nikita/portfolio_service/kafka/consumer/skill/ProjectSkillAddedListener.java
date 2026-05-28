package korobkin.nikita.portfolio_service.kafka.consumer.skill;

import korobkin.nikita.events.ProjectSkillAddedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioProjectSkillService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillAddedListener {

    private final PortfolioProjectSkillService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectSkillAdded}",
            containerFactory = "projectSkillAddedKafkaListenerContainerFactory"
    )
    public void handleProjectSkillAdded(ProjectSkillAddedEvent event) {
        log.info("received ProjectSkillAddedEvent: {}", event);

        processedEventService.process(
                event.eventId(),
                () -> portfolioService.addPortfolioProjectSkill(event)
        );
    }
}
