package korobkin.nikita.portfolio_service.mapper;

import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {

    Portfolio toEntity(UserProfileUpdatedEvent event);
}
