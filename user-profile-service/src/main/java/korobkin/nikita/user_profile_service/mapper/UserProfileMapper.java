package korobkin.nikita.user_profile_service.mapper;

import korobkin.nikita.events.UserProfileAvatarUpdatedEvent;
import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(source = "userId", target = "userId")
    UserProfileResponse toDto(UserProfile userProfile);

    @Mapping(target = "userId", ignore = true)
    @Mapping(source = "links", target = "links")
    void updateEntityFromDto(UpdateUserProfileRequest request, @MappingTarget UserProfile user);

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    UserProfileUpdatedEvent toUpdatedEvent(UserProfile userProfile);

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    UserProfileAvatarUpdatedEvent toAvatarUpdatedEvent(UserProfile userProfile);
}
