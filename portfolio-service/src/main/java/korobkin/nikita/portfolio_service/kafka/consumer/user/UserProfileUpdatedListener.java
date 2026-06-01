package korobkin.nikita.portfolio_service.kafka.consumer.user;

import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileUpdatedListener {

    private final PortfolioService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userProfileUpdated}",
            containerFactory = "userProfileUpdatedKafkaListenerContainerFactory"
    )
    public void handleUserProfileUpdated(UserProfileUpdatedEvent event) {

        log.info("Received UserProfileUpdatedEvent eventId={}, userId={}",
                event.eventId(),
                event.userId()
        );

        try {
            processedEventService.process(
                    event.eventId(),
                    () -> portfolioService.createPortfolio(event)
            );

            log.info(
                    "UserProfileUpdatedEvent processed eventId={}",
                    event.eventId()
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to process UserProfileUpdatedEvent eventId={}",
                    event.eventId(),
                    ex
            );

            throw ex;
        }
    }
}
