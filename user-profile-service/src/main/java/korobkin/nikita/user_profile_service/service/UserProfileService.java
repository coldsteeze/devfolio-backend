package korobkin.nikita.user_profile_service.service;

import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;

import java.util.UUID;

public interface UserProfileService {

    void createUserEmptyProfile(UserCreatedEvent event);

    UserProfileResponse getUserProfile(UUID id);

    UserProfileResponse fillUserProfile(UUID id, UpdateUserProfileRequest request);
}
