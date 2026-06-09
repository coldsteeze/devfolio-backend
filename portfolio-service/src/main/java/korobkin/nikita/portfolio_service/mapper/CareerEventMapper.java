package korobkin.nikita.portfolio_service.mapper;

import korobkin.nikita.events.CareerEntryPayload;
import korobkin.nikita.portfolio_service.entity.PortfolioCareerEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CareerEventMapper {

    @Mapping(target = "portfolio", ignore = true)
    @Mapping(target = "startMonth", source = "startDate.month")
    @Mapping(target = "startYear", source = "startDate.year")
    @Mapping(target = "endMonth", source = "endDate.month")
    @Mapping(target = "endYear", source = "endDate.year")
    PortfolioCareerEntry toEntity(CareerEntryPayload payload);

    List<PortfolioCareerEntry> toEntities(List<CareerEntryPayload> payloads);
}
