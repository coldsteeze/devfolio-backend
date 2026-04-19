package korobkin.nikita.portfolio_service.fixtures;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.events.UserProfileUpdatedEvent;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class PortfolioEventFixtures {

    public static UserProfileUpdatedEvent create(UUID userId) {
        return new UserProfileUpdatedEvent(
                userId,
                "nick_" + UUID.randomUUID(),
                "John",
                "Doe",
                "bio"
        );
    }

    public static UserProfileUpdatedEvent update(UUID userId) {
        return new UserProfileUpdatedEvent(
                userId,
                "newNick",
                "New",
                "Name",
                "newBio"
        );
    }

    public static UserDeletedEvent delete(UUID userId) {
        return new UserDeletedEvent(userId);
    }
}
