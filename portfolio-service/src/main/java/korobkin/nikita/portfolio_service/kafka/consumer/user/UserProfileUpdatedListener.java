package korobkin.nikita.portfolio_service.kafka.consumer.user;

import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileUpdatedListener {

    private final PortfolioService portfolioService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userProfileUpdated}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserProfileUpdated(UserProfileUpdatedEvent event) {
        log.info("received UserProfileUpdatedEvent: {}", event);
        portfolioService.createPortfolio(event);
    }
}
