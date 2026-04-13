package korobkin.nikita.portfolio_service.kafka.consumer.skill;

import korobkin.nikita.events.ProjectSkillsUpdatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillUpdatedListener {

    private final PortfolioService portfolioService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectSkillsUpdated}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleProjectSkillsUpdated(ProjectSkillsUpdatedEvent event) {
        log.info("received ProjectSkillUpdatedEvent: {}", event);
        portfolioService.updatePortfolioProjectSkill(event);
    }
}
