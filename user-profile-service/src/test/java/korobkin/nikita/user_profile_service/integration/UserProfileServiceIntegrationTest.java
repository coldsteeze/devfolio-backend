package korobkin.nikita.user_profile_service.integration;

import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.user_profile_service.dto.response.PagedResponse;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.exception.NicknameAlreadyTakenException;
import korobkin.nikita.user_profile_service.exception.UserProfileNotFoundException;
import korobkin.nikita.user_profile_service.fixtures.UserProfileFixtures;
import korobkin.nikita.user_profile_service.fixtures.UserProfileRequestFixtures;
import korobkin.nikita.user_profile_service.kafka.producer.UserEventProducer;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;

public class UserProfileServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @MockitoBean
    UserEventProducer producer;

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
        assertThat(filledProfile.getAvatarUrl()).isEqualTo(UserProfileRequestFixtures.DEFAULT_AVATAR_URL);
        assertThat(filledProfile.getSkills()).isEqualTo(UserProfileRequestFixtures.DEFAULT_SKILLS);
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
        assertThat(updatedProfile.getAvatarUrl()).isEqualTo(UserProfileRequestFixtures.DEFAULT_AVATAR_URL);
        assertThat(updatedProfile.getSkills()).isEqualTo(UserProfileRequestFixtures.DEFAULT_SKILLS);
        assertThat(updatedProfile.getLinks()).isEqualTo(UserProfileRequestFixtures.DEFAULT_LINKS);
    }

    @Test
    void updateAvatar_success() {
        UserProfile profile = createProfile();

        UserProfileResponse result = userProfileService.updateUserProfileAvatar(
                profile.getUserId(),
                UserProfileRequestFixtures.updateUserProfileAvatarRequest()
        );

        UserProfile updatedAvatarProfile = userProfileRepository.findById(profile.getUserId()).orElseThrow();

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(profile.getUserId());
        assertThat(updatedAvatarProfile.getNickname()).isEqualTo(UserProfileFixtures.DEFAULT_NICKNAME);
        assertThat(updatedAvatarProfile.getAvatarUrl()).isEqualTo(UserProfileRequestFixtures.NEW_AVATAR_URL);
    }

    @Test
    void updateAvatar_updatesUpdatedAt() {
        UserProfile profile = createProfile();

        UserProfileResponse result = userProfileService.updateUserProfileAvatar(
                profile.getUserId(),
                UserProfileRequestFixtures.updateUserProfileAvatarRequest()
        );

        UserProfile updatedAvatarProfile = userProfileRepository.findById(profile.getUserId()).orElseThrow();

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(profile.getUserId());
        assertThat(updatedAvatarProfile.getNickname()).isEqualTo(UserProfileFixtures.DEFAULT_NICKNAME);
        assertThat(updatedAvatarProfile.getAvatarUrl()).isEqualTo(UserProfileRequestFixtures.NEW_AVATAR_URL);
        assertThat(updatedAvatarProfile.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateAvatar_otherFields_notUpdated() {
        UserProfile profile = createProfile();

        UserProfileResponse result = userProfileService.updateUserProfileAvatar(
                profile.getUserId(),
                UserProfileRequestFixtures.updateUserProfileAvatarRequest()
        );

        UserProfile updatedAvatarProfile = userProfileRepository.findById(profile.getUserId()).orElseThrow();

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(profile.getUserId());
        assertThat(updatedAvatarProfile.getNickname()).isEqualTo(UserProfileFixtures.DEFAULT_NICKNAME);
    }

    @Test
    void findProfiles_emptySkills_returnsAll() {
        createProfile(UserProfileFixtures.FIRST_USER_NICKNAME);
        createProfile(UserProfileFixtures.SECOND_USER_NICKNAME);

        PagedResponse<UserProfileResponse> result = userProfileService.findBySkills(new HashSet<>(), PageRequest.of(0, 10));

        assertThat(result.content())
                .extracting(UserProfileResponse::nickname)
                .containsExactlyInAnyOrder(
                        UserProfileFixtures.FIRST_USER_NICKNAME,
                        UserProfileFixtures.SECOND_USER_NICKNAME
                );
    }

    @Test
    void findProfiles_withSkill_returnsMatching() {
        createProfile(UserProfileFixtures.FIRST_USER_NICKNAME, Set.of(UserProfileFixtures.FIRST_USER_SKILL));
        createProfile(UserProfileFixtures.SECOND_USER_NICKNAME, Set.of(UserProfileFixtures.SECOND_USER_SKILL));

        PagedResponse<UserProfileResponse> result = userProfileService.findBySkills(
                Set.of(UserProfileFixtures.FIRST_USER_SKILL),
                PageRequest.of(0, 10)

        );

        assertThat(result.content())
                .extracting(UserProfileResponse::nickname)
                .containsExactly(UserProfileFixtures.FIRST_USER_NICKNAME);
    }

    @Test
    void findProfiles_withPageable_returnsCorrectPage() {
        UserProfile profile = createProfile();

        PagedResponse<UserProfileResponse> result = userProfileService.findBySkills(new HashSet<>(), PageRequest.of(0, 10));

        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.content())
                .extracting(UserProfileResponse::userId)
                .containsExactly(profile.getUserId());
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

    private void createProfile(String nickname, Set<String> skills) {
        UserProfile profile = UserProfileFixtures.builder()
                .withUserId(UUID.randomUUID())
                .withNickname(nickname)
                .withSkills(skills)
                .build();

        userProfileRepository.save(profile);
    }
}

