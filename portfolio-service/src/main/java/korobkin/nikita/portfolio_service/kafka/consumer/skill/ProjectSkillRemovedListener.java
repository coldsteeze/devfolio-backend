package korobkin.nikita.portfolio_service.kafka.consumer.skill;

import korobkin.nikita.events.ProjectSkillRemovedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioProjectSkillService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillRemovedListener {

    private final PortfolioProjectSkillService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectSkillRemoved}",
            containerFactory = "projectSkillRemovedKafkaListenerContainerFactory"
    )
    public void handleProjectSkillRemoved(ProjectSkillRemovedEvent event) {
        log.info("received ProjectSkillRemovedEvent: {}", event);

        processedEventService.process(
                event.eventId(),
                () -> portfolioService.deletePortfolioProjectSkill(event)
        );
    }
}
