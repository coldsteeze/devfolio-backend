package korobkin.nikita.user_profile_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileAvatarRequest;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserProfileControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserProfileService userProfileService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userProfileRepository.deleteAll();
    }

    @Test
    void getMyProfile_shouldReturnProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setNickname("testuser");
        userProfileRepository.save(profile);

        mockMvc.perform(get("/api/profiles/me")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new UserPrincipal(userId, "test@mail.com"),
                                null,
                                Collections.emptyList()
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.nickname").value("testuser"));
    }

    @Test
    void fillMyProfile_shouldCreateProfile() throws Exception {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setNickname("newuser");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setBio("Bio");
        request.setAvatarUrl("http://avatar.url");
        request.setSkills(Set.of("skill1", "skill2"));
        request.setLinks(Map.of("github", "https://github.com"));
        UserCreatedEvent event = new UserCreatedEvent(userId);
        userProfileService.createUserEmptyProfile(event);

        mockMvc.perform(post("/api/profiles")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new UserPrincipal(userId, "test@mail.com"),
                                null,
                                Collections.emptyList()
                        )))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("newuser"))
                .andExpect(jsonPath("$.firstName").value("First"));
    }

    @Test
    void updateMyProfile_shouldUpdateProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setNickname("olduser");
        userProfileRepository.save(profile);

        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setNickname("updatedUser");
        request.setFirstName("UpdatedFirst");
        request.setLastName("UpdatedLast");
        request.setBio("UpdatedBio");

        mockMvc.perform(put("/api/profiles/me")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new UserPrincipal(userId, "test@mail.com"),
                                null,
                                Collections.emptyList()
                        )))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("updatedUser"))
                .andExpect(jsonPath("$.firstName").value("UpdatedFirst"));
    }

    @Test
    void updateProfileAvatar_shouldUpdateAvatar() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        userProfileRepository.save(profile);

        UpdateUserProfileAvatarRequest request = new UpdateUserProfileAvatarRequest();
        request.setAvatarUrl("http://new.avatar.url");

        mockMvc.perform(patch("/api/profiles/me/avatar")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new UserPrincipal(userId, "test@mail.com"),
                                null,
                                Collections.emptyList()
                        )))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl").value("http://new.avatar.url"));
    }

    @Test
    void deleteMyProfile_shouldDeleteProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        userProfileRepository.save(profile);

        mockMvc.perform(delete("/api/profiles/me")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new UserPrincipal(userId, "test@mail.com"),
                                null,
                                Collections.emptyList()
                        ))))
                .andExpect(status().isNoContent());

        assertThat(userProfileRepository.findById(userId)).isEmpty();
    }

    @Test
    void searchProfilesBySkills_shouldReturnProfiles() throws Exception {
        UserProfile profile1 = new UserProfile();
        profile1.setUserId(UUID.randomUUID());
        profile1.setNickname("user1");
        profile1.setSkills(Set.of("skill1"));
        userProfileRepository.save(profile1);

        UserProfile profile2 = new UserProfile();
        profile2.setUserId(UUID.randomUUID());
        profile2.setNickname("user2");
        profile2.setSkills(Set.of("skill2"));
        userProfileRepository.save(profile2);

        mockMvc.perform(get("/api/profiles")
                        .param("skills", "skill1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nickname").value("user1"));
    }
}
