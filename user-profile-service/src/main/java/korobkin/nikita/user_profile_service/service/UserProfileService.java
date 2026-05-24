package korobkin.nikita.user_profile_service.service;

import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.MediaResponse;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserProfileService {

    void createUserEmptyProfile(UserCreatedEvent event);

    UserProfileResponse getUserProfile(UUID id);

    UserProfileResponse fillUserProfile(UUID id, UpdateUserProfileRequest request);

    UserProfileResponse updateUserProfile(UUID id, UpdateUserProfileRequest request);

    void deleteUserProfile(UUID id);

    MediaResponse uploadUserProfileAvatar(MultipartFile file, UserPrincipal principal);

    void deleteUserProfileAvatar(UserPrincipal principal);
}
