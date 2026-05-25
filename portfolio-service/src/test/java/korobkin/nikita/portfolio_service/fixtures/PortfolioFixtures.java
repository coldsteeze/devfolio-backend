package korobkin.nikita.portfolio_service.fixtures;

import korobkin.nikita.events.UserType;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class PortfolioFixtures {

    public static final String NICKNAME = "nick";
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Doe";
    public static final String BIO = "bio";

    public static final String OLD_NICKNAME = "oldNick";
    public static final String NEW_NICKNAME = "newNick";

    public static Portfolio portfolio(UUID userId, String nickname) {
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId(userId);
        portfolio.setNickname(nickname);
        portfolio.setFirstName(FIRST_NAME);
        portfolio.setLastName(LAST_NAME);
        portfolio.setBio(BIO);
        portfolio.setTotalProjects((short) 0);
        portfolio.setUserType(UserType.JOB_SEEKER);
        return portfolio;
    }

    public static Portfolio valid(UUID userId) {
        return portfolio(userId, NICKNAME);
    }

    public static Portfolio old(UUID userId) {
        return portfolio(userId, OLD_NICKNAME);
    }

    public static Portfolio updated(UUID userId) {
        return portfolio(userId, NEW_NICKNAME);
    }
}
