package korobkin.nikita.user_profile_service.integration;

import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileAvatarRequest;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.exception.NicknameAlreadyTakenException;
import korobkin.nikita.user_profile_service.exception.UserProfileNotFoundException;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class UserProfileIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @BeforeEach
    void setUp() {
        userProfileRepository.deleteAll();
    }

    @Test
    void getProfile_success() {
        UserProfile profile = createProfile(UUID.randomUUID(), "testuser");

        var result = userProfileService.getUserProfile(profile.getUserId());

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(profile.getUserId());
        assertThat(result.getNickname()).isEqualTo("testuser");
    }

    @Test
    void fillProfile_allFields_success() {
        UserProfile profile = createProfile(UUID.randomUUID(), "testuser");

        UpdateUserProfileRequest updateUserProfileRequest = getUpdateUserProfileRequest();

        var result = userProfileService.fillUserProfile(profile.getUserId(), updateUserProfileRequest);
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(profile.getUserId());
        assertThat(result.getNickname()).isEqualTo("testuser");
        assertThat(result.getFirstName()).isEqualTo("testuser first name");
        assertThat(result.getLastName()).isEqualTo("testuser last name");
        assertThat(result.getBio()).isEqualTo("testuser bio");
        assertThat(result.getAvatarUrl()).isEqualTo("http://localhost:8080/avatar");
        assertThat(result.getSkills().contains("skill1")).isTrue();
        assertThat(result.getSkills().contains("skill2")).isTrue();
        assertThat(result.getLinks().containsKey("github")).isTrue();
        assertThat(result.getLinks().get("github")).isNotNull();
    }

    @Test
    void updateProfile_partialFields_success() {
        UserProfile profile = createProfile(UUID.randomUUID(), "testuser");

        UpdateUserProfileRequest updateUserProfileRequest = getUpdateUserProfileRequest();

        var result = userProfileService.updateUserProfile(profile.getUserId(), updateUserProfileRequest);
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(profile.getUserId());
        assertThat(result.getNickname()).isEqualTo("testuser");
        assertThat(result.getFirstName()).isEqualTo("testuser first name");
        assertThat(result.getLastName()).isEqualTo("testuser last name");
        assertThat(result.getBio()).isEqualTo("testuser bio");
        assertThat(result.getAvatarUrl()).isEqualTo("http://localhost:8080/avatar");
        assertThat(result.getSkills().contains("skill1")).isTrue();
        assertThat(result.getSkills().contains("skill2")).isTrue();
        assertThat(result.getLinks().containsKey("github")).isTrue();
        assertThat(result.getLinks().get("github")).isNotNull();
    }

    @Test
    void updateAvatar_success() {
        UserProfile profile = createProfile(UUID.randomUUID(), "testuser");

        UpdateUserProfileAvatarRequest request = new UpdateUserProfileAvatarRequest();
        request.setAvatarUrl("http://localhost:8080/avatar");
        var result = userProfileService.updateUserProfileAvatar(profile.getUserId(), request);
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(profile.getUserId());
        assertThat(result.getNickname()).isEqualTo("testuser");
        assertThat(result.getAvatarUrl()).isEqualTo("http://localhost:8080/avatar");
    }

    @Test
    void updateAvatar_updatesUpdatedAt() {
        UserProfile profile = createProfile(UUID.randomUUID(), "testuser");

        UpdateUserProfileAvatarRequest updateUserProfileAvatarRequest = new UpdateUserProfileAvatarRequest();
        updateUserProfileAvatarRequest.setAvatarUrl("http://localhost:8080/avatar");
        var result = userProfileService.updateUserProfileAvatar(profile.getUserId(), updateUserProfileAvatarRequest);
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(profile.getUserId());
        assertThat(result.getNickname()).isEqualTo("testuser");
        assertThat(result.getAvatarUrl()).isEqualTo("http://localhost:8080/avatar");
        profile = userProfileRepository.findById(profile.getUserId()).get();
        assertThat(profile.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateAvatar_otherFields_notUpdated() {
        UserProfile profile = createProfile(UUID.randomUUID(), "testuser");

        UpdateUserProfileAvatarRequest updateUserProfileAvatarRequest = new UpdateUserProfileAvatarRequest();
        updateUserProfileAvatarRequest.setAvatarUrl("http://localhost:8080/avatar");
        var result = userProfileService.updateUserProfileAvatar(profile.getUserId(), updateUserProfileAvatarRequest);
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(profile.getUserId());
        assertThat(result.getNickname()).isEqualTo("testuser");
    }

    @Test
    void findProfiles_emptySkills_returnsAll() {
        createProfile(UUID.randomUUID(), "testuser1");
        createProfile(UUID.randomUUID(), "testuser2");

        List<UserProfile> userProfiles = userProfileRepository.findAll();
        assertThat(userProfiles).isNotNull();
        var result = userProfileService.findBySkills(new HashSet<>(), PageRequest.of(0, 10));
        assertThat(userProfiles.size()).isEqualTo(result.getContent().size());
    }

    @Test
    void findProfiles_withSkill_returnsMatching() {
        createProfile(UUID.randomUUID(), "testuser1", Set.of("skill1", "skill2"));
        createProfile(UUID.randomUUID(), "testuser2", Set.of("skill1", "skill2"));

        List<UserProfile> userProfiles = userProfileRepository.findAll();
        assertThat(userProfiles).isNotNull();
        var result = userProfileService.findBySkills(Set.of("skill1"), PageRequest.of(0, 10));
        assertThat(userProfiles.size()).isEqualTo(result.getContent().size());
    }

    @Test
    void findProfiles_withPageable_returnsCorrectPage() {
        createProfile(UUID.randomUUID(), "testuser1");

        List<UserProfile> userProfiles = userProfileRepository.findAll();
        assertThat(userProfiles).isNotNull();

        var result = userProfileService.findBySkills(new HashSet<>(), PageRequest.of(0, 10));

        assertThat(userProfiles.size()).isEqualTo(result.getContent().size());
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    void deleteProfile_success() {
        UserProfile profile = createProfile(UUID.randomUUID(), "testuser");

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
        createProfile(UUID.randomUUID(), "testuser");

        UserProfile profile2 = createProfile(UUID.randomUUID(), "testuser1");

        assertThatThrownBy(() -> userProfileService
                .fillUserProfile(profile2.getUserId(), getUpdateUserProfileRequest()))
                .isInstanceOf(NicknameAlreadyTakenException.class)
                .hasMessageContaining("Nickname already exists");
    }

    @Test
    void updateProfile_duplicateNickname_throwsException() {
        createProfile(UUID.randomUUID(), "testuser");

        UserProfile profile2 = createProfile(UUID.randomUUID(), "testuser1");

        assertThatThrownBy(() -> userProfileService
                .updateUserProfile(profile2.getUserId(), getUpdateUserProfileRequest()))
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
        UserProfile userProfile = createProfile(UUID.randomUUID(), "testuser");

        userProfileService.deleteUserProfile(userProfile.getUserId());

        assertThat(userProfileRepository.findById(userProfile.getUserId())).isEmpty();
    }

    private static @NotNull UpdateUserProfileRequest getUpdateUserProfileRequest() {
        UpdateUserProfileRequest updateUserProfileRequest = new UpdateUserProfileRequest();
        updateUserProfileRequest.setNickname("testuser");
        updateUserProfileRequest.setFirstName("testuser first name");
        updateUserProfileRequest.setLastName("testuser last name");
        updateUserProfileRequest.setBio("testuser bio");
        updateUserProfileRequest.setAvatarUrl("http://localhost:8080/avatar");
        Set<String> skills = new HashSet<>();
        skills.add("skill1");
        skills.add("skill2");
        updateUserProfileRequest.setSkills(skills);
        Map<String, String> links = new HashMap<>();
        links.put("github", "https://github.com");
        updateUserProfileRequest.setLinks(links);
        return updateUserProfileRequest;
    }

    private UserProfile createProfile(UUID userId, String nickname) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setNickname(nickname);
        return userProfileRepository.save(profile);
    }

    private void createProfile(UUID userId, String nickname, Set<String> skills) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setNickname(nickname);
        profile.setSkills(skills);
        userProfileRepository.save(profile);
    }
}

