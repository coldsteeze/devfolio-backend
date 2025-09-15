package korobkin.nikita.user_profile_service.service;

import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileAvatarRequest;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface UserProfileService {

    void createUserEmptyProfile(UserCreatedEvent event);

    UserProfileResponse getUserProfile(UUID id);

    UserProfileResponse fillUserProfile(UUID id, UpdateUserProfileRequest request);

    UserProfileResponse updateUserProfile(UUID id, UpdateUserProfileRequest request);

    UserProfileResponse updateUserProfileAvatar(UUID id, UpdateUserProfileAvatarRequest request);

    Page<UserProfileResponse> findBySkills(Set<String> skills, Pageable pageable);

    void deleteUserProfile(UUID id);
}
