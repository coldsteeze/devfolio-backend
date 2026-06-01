package korobkin.nikita.portfolio_service.kafka.consumer.user;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDeletedListener {

    private final PortfolioService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userDeleted}",
            containerFactory = "userDeletedKafkaListenerContainerFactory"
    )
    public void handleUserDeleted(UserDeletedEvent event) {

        log.info("Received UserDeletedEvent eventId={}, userId={}",
                event.eventId(),
                event.userId()
        );

        try {
            processedEventService.process(
                    event.eventId(),
                    () -> portfolioService.deletePortfolio(event)
            );

            log.info(
                    "UserDeletedEvent processed eventId={}",
                    event.eventId()
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to process UserDeletedEvent eventId={}",
                    event.eventId(),
                    ex
            );

            throw ex;
        }
    }
}
