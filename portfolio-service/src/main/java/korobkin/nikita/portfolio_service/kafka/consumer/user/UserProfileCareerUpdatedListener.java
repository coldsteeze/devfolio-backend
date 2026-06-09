package korobkin.nikita.portfolio_service.kafka.consumer.user;

import korobkin.nikita.events.UserProfileCareerUpdatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileCareerUpdatedListener {

    private final PortfolioService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userProfileCareerUpdated}",
            containerFactory = "userProfileCareerUpdatedKafkaListenerContainerFactory"
    )
    public void handleCareerUpdated(UserProfileCareerUpdatedEvent event) {
        log.info("Received UserProfileCareerUpdatedEvent eventId={}, userId={}",
                event.eventId(),
                event.userId()
        );

        try {
            processedEventService.process(
                    event.eventId(),
                    () -> portfolioService.updatePortfolioCareerEntry(event)
            );

            log.info(
                    "UserProfileCareerUpdatedEvent processed eventId={}",
                    event.eventId()
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to process UserProfileCareerUpdatedEvent eventId={}",
                    event.eventId(),
                    ex
            );

            throw ex;
        }
    }
}
