package korobkin.nikita.user_profile_service.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import korobkin.nikita.jwtsecuritystarter.config.JwtProperties;
import korobkin.nikita.jwtsecuritystarter.security.jwt.JwtService;
import korobkin.nikita.user_profile_service.controller.UserProfileController;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileAvatarRequest;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.PagedResponse;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.fixtures.UserProfileRequestFixtures;
import korobkin.nikita.user_profile_service.fixtures.UserProfileResponseFixtures;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserProfileControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserProfileService userProfileService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtProperties jwtProperties;

    private UUID userId;
    private String authenticationEmail;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        authenticationEmail = "test@mail.com";
    }

    @Test
    void getMyProfile_success() throws Exception {
        given(userProfileService.getUserProfile(any(UUID.class)))
                .willReturn(getUserProfileResponse(userId));

        mockMvc.perform(get("/api/profiles/me")
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, authenticationEmail))))
                .andExpect(status().isOk())
                .andExpectAll(expectUserProfile(userId));
    }

    @Test
    void fillMyProfile_success() throws Exception {
        given(userProfileService.fillUserProfile(any(UUID.class), any(UpdateUserProfileRequest.class)))
                .willReturn(getUserProfileResponse(userId));

        mockMvc.perform(post("/api/profiles")
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, authenticationEmail)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(getUserProfileRequest())))
                .andExpect(status().isCreated())
                .andExpectAll(expectUserProfile(userId));
    }

    @Test
    void getUserProfile_success() throws Exception {
        given(userProfileService.getUserProfile(any(UUID.class)))
                .willReturn(getUserProfileResponse(userId));

        mockMvc.perform(get("/api/profiles/{userId}", userId))
                .andExpect(status().isOk())
                .andExpectAll(expectUserProfile(userId));
    }

    @Test
    void updateMyProfile_success() throws Exception {
        given(userProfileService.updateUserProfile(any(UUID.class), any(UpdateUserProfileRequest.class)))
                .willReturn(getUserProfileResponse(userId));

        mockMvc.perform(put("/api/profiles/me")
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, authenticationEmail)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(getUserProfileRequest())))
                .andExpect(status().isOk())
                .andExpectAll(expectUserProfile(userId));
    }

    @Test
    void updateProfileAvatar_success() throws Exception {
        given(userProfileService.updateUserProfileAvatar(any(UUID.class), any(UpdateUserProfileAvatarRequest.class)))
                .willReturn(getUserProfileResponse(userId));

        UpdateUserProfileAvatarRequest request = UserProfileRequestFixtures.updateUserProfileAvatarRequest(
                UserProfileResponseFixtures.DEFAULT_AVATAR_URL
        );

        mockMvc.perform(patch("/api/profiles/me/avatar")
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, authenticationEmail)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpectAll(expectUserProfile(userId));
    }

    @Test
    void searchProfilesBySkills_success() throws Exception {
        PagedResponse<UserProfileResponse> page = new PagedResponse<>(List.of(getUserProfileResponse(userId)), 0, 1, 1, 1);
        given(userProfileService.findBySkills(any(), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/profiles")
                        .param("skills", UserProfileResponseFixtures.VALUE_SKILL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$.content[0].nickname").value(UserProfileResponseFixtures.DEFAULT_NICKNAME))
                .andExpect(jsonPath("$.content[0].firstName").value(UserProfileResponseFixtures.DEFAULT_FIRST_NAME))
                .andExpect(jsonPath("$.content[0].lastName").value(UserProfileResponseFixtures.DEFAULT_LAST_NAME))
                .andExpect(jsonPath("$.content[0].bio").value(UserProfileResponseFixtures.DEFAULT_BIO))
                .andExpect(jsonPath("$.content[0].avatarUrl").value(UserProfileResponseFixtures.DEFAULT_AVATAR_URL))
                .andExpect(jsonPath("$.content[0].skills[0]").value(UserProfileResponseFixtures.VALUE_SKILL))
                .andExpect(jsonPath("$.content[0].links.github").value(UserProfileResponseFixtures.VALUE_LINK));
    }

    @Test
    void deleteMyProfile_success() throws Exception {
        willDoNothing().given(userProfileService).deleteUserProfile(any(UUID.class));

        mockMvc.perform(delete("/api/profiles/me")
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, authenticationEmail))))
                .andExpect(status().isNoContent());
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    private UserProfileResponse getUserProfileResponse(UUID userId) {
        return UserProfileResponseFixtures.getDefault(userId);
    }

    private UpdateUserProfileRequest getUserProfileRequest() {
        return UserProfileRequestFixtures.updateUserProfileRequest(
                UserProfileResponseFixtures.DEFAULT_NICKNAME,
                UserProfileResponseFixtures.DEFAULT_FIRST_NAME,
                UserProfileResponseFixtures.DEFAULT_LAST_NAME,
                UserProfileResponseFixtures.DEFAULT_BIO,
                UserProfileResponseFixtures.DEFAULT_AVATAR_URL,
                UserProfileResponseFixtures.DEFAULT_SKILLS,
                UserProfileRequestFixtures.DEFAULT_LINKS
        );
    }

    private ResultMatcher[] expectUserProfile(UUID userId) {
        return new ResultMatcher[]{
                jsonPath("$.userId").value(userId.toString()),
                jsonPath("$.nickname").value(UserProfileResponseFixtures.DEFAULT_NICKNAME),
                jsonPath("$.firstName").value(UserProfileResponseFixtures.DEFAULT_FIRST_NAME),
                jsonPath("$.lastName").value(UserProfileResponseFixtures.DEFAULT_LAST_NAME),
                jsonPath("$.bio").value(UserProfileResponseFixtures.DEFAULT_BIO),
                jsonPath("$.avatarUrl").value(UserProfileResponseFixtures.DEFAULT_AVATAR_URL),
                jsonPath("$.skills").value(UserProfileResponseFixtures.VALUE_SKILL),
                jsonPath("$.links.github").value(UserProfileResponseFixtures.VALUE_LINK)
        };
    }
}

