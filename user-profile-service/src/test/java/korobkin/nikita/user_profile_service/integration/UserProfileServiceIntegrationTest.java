package korobkin.nikita.user_profile_service.integration;

import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.user_profile_service.client.MediaClient;
import korobkin.nikita.user_profile_service.dto.response.MediaResponse;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.exception.NicknameAlreadyTakenException;
import korobkin.nikita.user_profile_service.exception.UserProfileNotFoundException;
import korobkin.nikita.user_profile_service.fixtures.UserProfileFixtures;
import korobkin.nikita.user_profile_service.fixtures.UserProfileRequestFixtures;
import korobkin.nikita.user_profile_service.kafka.producer.UserProfileDeletedEventProducer;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserProfileServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @MockitoBean
    UserProfileDeletedEventProducer producer;

    @MockitoBean
    private MediaClient mediaClient;

    @Test
    void getProfile_success() {
        UserProfile profile = createProfile();

        UserProfileResponse result = userProfileService.getUserProfile(profile.getUserId());

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(profile.getUserId());
        assertThat(result.nickname()).isEqualTo(UserProfileFixtures.DEFAULT_NICKNAME);
    }

    @Test
    void fillProfile_allFields_success() {
        UserProfile profile = createProfile();

        UserProfileResponse result = userProfileService.fillUserProfile(
                profile.getUserId(),
                UserProfileRequestFixtures.updateUserProfileRequest()
        );

        UserProfile filledProfile = userProfileRepository.findById(profile.getUserId()).orElseThrow();

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(filledProfile.getUserId());
        assertThat(filledProfile.getNickname()).isEqualTo(UserProfileRequestFixtures.DEFAULT_NICKNAME);
        assertThat(filledProfile.getFirstName()).isEqualTo(UserProfileRequestFixtures.DEFAULT_FIRST_NAME);
        assertThat(filledProfile.getLastName()).isEqualTo(UserProfileRequestFixtures.DEFAULT_LAST_NAME);
        assertThat(filledProfile.getBio()).isEqualTo(UserProfileRequestFixtures.DEFAULT_BIO);
        assertThat(filledProfile.getLinks()).isEqualTo(UserProfileRequestFixtures.DEFAULT_LINKS);
    }

    @Test
    void updateProfile_partialFields_success() {
        UserProfile profile = createProfile();


        UserProfileResponse result = userProfileService.updateUserProfile(
                profile.getUserId(),
                UserProfileRequestFixtures.updateUserProfileRequest()
        );

        UserProfile updatedProfile = userProfileRepository.findById(profile.getUserId()).orElseThrow();

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(profile.getUserId());
        assertThat(updatedProfile.getNickname()).isEqualTo(UserProfileRequestFixtures.DEFAULT_NICKNAME);
        assertThat(updatedProfile.getFirstName()).isEqualTo(UserProfileRequestFixtures.DEFAULT_FIRST_NAME);
        assertThat(updatedProfile.getLastName()).isEqualTo(UserProfileRequestFixtures.DEFAULT_LAST_NAME);
        assertThat(updatedProfile.getBio()).isEqualTo(UserProfileRequestFixtures.DEFAULT_BIO);
        assertThat(updatedProfile.getLinks()).isEqualTo(UserProfileRequestFixtures.DEFAULT_LINKS);
    }

    @Test
    void uploadAvatar_success() {
        UserProfile profile = createProfile();

        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("test.png");

        MediaResponse response = new MediaResponse("http://localhost/file.png");

        when(mediaClient.upload(any(), eq("user-profile/avatars")))
                .thenReturn(response);

        MediaResponse result = userProfileService.uploadUserProfileAvatar(file,
                new UserPrincipal(profile.getUserId(), "test@mail.com"));

        assertThat(result).isNotNull();
    }

    @Test
    void deleteProfile_success() {
        UserProfile profile = createProfile();

        assertThat(userProfileRepository.findById(profile.getUserId())).isNotNull();
        userProfileService.deleteUserProfile(profile.getUserId());

        assertThat(userProfileRepository.findById(profile.getUserId())).isEmpty();
    }

    @Test
    void deleteProfile_notExists_doesNothing() {
        UUID nonExistentUserId = UUID.randomUUID();

        assertThatCode(() -> userProfileService.deleteUserProfile(nonExistentUserId))
                .doesNotThrowAnyException();

        assertThat(userProfileRepository.findById(nonExistentUserId)).isNotPresent();
        verify(producer).sendUserDeleted(new UserDeletedEvent(nonExistentUserId));
    }

    @Test
    void getProfile_notExists_throwsException() {
        UUID nonExistentUserId = UUID.randomUUID();

        assertThatThrownBy(() -> userProfileService.getUserProfile(nonExistentUserId))
                .isInstanceOf(UserProfileNotFoundException.class)
                .hasMessageContaining("User with this id not found");
    }

    @Test
    void fillProfile_duplicateNickname_throwsException() {
        createProfile(UserProfileFixtures.DEFAULT_NICKNAME);

        UserProfile profile2 = createProfile(UserProfileFixtures.SECOND_USER_NICKNAME);

        assertThatThrownBy(() -> userProfileService
                .fillUserProfile(profile2.getUserId(), UserProfileRequestFixtures.updateUserProfileRequest()))
                .isInstanceOf(NicknameAlreadyTakenException.class)
                .hasMessageContaining("Nickname already exists");
    }

    @Test
    void updateProfile_duplicateNickname_throwsException() {
        createProfile(UserProfileFixtures.DEFAULT_NICKNAME);

        UserProfile profile2 = createProfile(UserProfileFixtures.SECOND_USER_NICKNAME);

        assertThatThrownBy(() -> userProfileService
                .updateUserProfile(profile2.getUserId(), UserProfileRequestFixtures.updateUserProfileRequest()))
                .isInstanceOf(NicknameAlreadyTakenException.class)
                .hasMessageContaining("Nickname already exists");
    }

    @Test
    void createEmptyProfile_onUserCreatedEvent_success() {
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(UUID.randomUUID());

        userProfileService.createUserEmptyProfile(userCreatedEvent);

        assertThat(userProfileRepository.findById(userCreatedEvent.userId())).isPresent();
    }

    @Test
    void deleteProfile_onDeleteEvent_success() {
        UserProfile userProfile = createProfile();

        userProfileService.deleteUserProfile(userProfile.getUserId());

        verify(producer).sendUserDeleted(new UserDeletedEvent(userProfile.getUserId()));
        assertThat(userProfileRepository.findById(userProfile.getUserId())).isEmpty();
    }

    private UserProfile createProfile() {
        UserProfile profile = UserProfileFixtures.builder()
                .withUserId(UUID.randomUUID())
                .build();

        return userProfileRepository.save(profile);
    }

    private UserProfile createProfile(String nickname) {
        UserProfile profile = UserProfileFixtures.builder()
                .withUserId(UUID.randomUUID())
                .withNickname(nickname)
                .build();

        return userProfileRepository.save(profile);
    }
}

