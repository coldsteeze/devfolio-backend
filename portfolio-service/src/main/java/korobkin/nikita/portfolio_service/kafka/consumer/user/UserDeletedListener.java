package korobkin.nikita.portfolio_service.kafka.consumer.user;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDeletedListener {

    private final PortfolioService portfolioService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userDeleted}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("received UserDeletedEvent: {}", event);
        portfolioService.deletePortfolio(event);
    }
}
