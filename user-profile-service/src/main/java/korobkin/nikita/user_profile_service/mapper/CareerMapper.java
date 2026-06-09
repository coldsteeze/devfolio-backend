package korobkin.nikita.user_profile_service.mapper;

import korobkin.nikita.events.CareerDatePayload;
import korobkin.nikita.events.CareerEntryPayload;
import korobkin.nikita.events.UserProfileCareerUpdatedEvent;
import korobkin.nikita.user_profile_service.dto.request.CareerEntryRequest;
import korobkin.nikita.user_profile_service.dto.response.CareerDateResponse;
import korobkin.nikita.user_profile_service.dto.response.CareerEntryResponse;
import korobkin.nikita.user_profile_service.dto.response.CareerResponse;
import korobkin.nikita.user_profile_service.entity.CareerEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CareerMapper {

    @Mapping(target = "startDate",
            expression = "java(mapRestDate(entity.getStartMonth(), entity.getStartYear()))")
    @Mapping(target = "endDate",
            expression = "java(mapRestDate(entity.getEndMonth(), entity.getEndYear()))")
    CareerEntryResponse toResponse(CareerEntry entity);

    default CareerResponse toResponse(List<CareerEntry> entities) {
        if (entities == null) {
            return new CareerResponse(List.of());
        }

        return new CareerResponse(
                entities.stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "startMonth", source = "startDate.month")
    @Mapping(target = "startYear", source = "startDate.year")
    @Mapping(target = "endMonth", source = "endDate.month")
    @Mapping(target = "endYear", source = "endDate.year")
    CareerEntry toEntity(CareerEntryRequest request);

    default List<CareerEntry> toEntities(List<CareerEntryRequest> requests) {
        if (requests == null) {
            return List.of();
        }

        return requests.stream()
                .map(this::toEntity)
                .toList();
    }

    @Mapping(target = "startDate",
            expression = "java(mapEventDate(entity.getStartMonth(), entity.getStartYear()))")
    @Mapping(target = "endDate",
            expression = "java(mapEventDate(entity.getEndMonth(), entity.getEndYear()))")
    CareerEntryPayload toPayload(CareerEntry entity);

    default List<CareerEntryPayload> toPayloads(List<CareerEntry> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toPayload)
                .toList();
    }

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "career", expression = "java(toPayloads(entries))")
    @Mapping(target = "userId", source = "userId")
    UserProfileCareerUpdatedEvent toUpdatedEvent(UUID userId, List<CareerEntry> entries);

    default CareerDateResponse mapRestDate(Integer month, Integer year) {
        if (month == null || year == null) return null;
        return new CareerDateResponse(month, year);
    }

    default CareerDatePayload mapEventDate(Integer month, Integer year) {
        if (month == null || year == null) return null;
        return new CareerDatePayload(month, year);
    }
}
