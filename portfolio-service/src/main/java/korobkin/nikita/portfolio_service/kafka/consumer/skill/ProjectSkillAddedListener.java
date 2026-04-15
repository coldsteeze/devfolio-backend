package korobkin.nikita.portfolio_service.kafka.consumer.skill;

import korobkin.nikita.events.ProjectSkillAddedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioProjectSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillAddedListener {

    private final PortfolioProjectSkillService portfolioService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.projectSkillAdded}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleProjectSkillAdded(ProjectSkillAddedEvent event) {
        log.info("received ProjectSkillAddedEvent: {}", event);
        portfolioService.addPortfolioProjectSkill(event);
    }
}
