package korobkin.nikita.user_profile_service.mapper;

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
    void updateEntityFromDto(UpdateUserProfileRequest request, @MappingTarget UserProfile user);
}
