package korobkin.nikita.user_profile_service.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import korobkin.nikita.user_profile_service.config.JwtProperties;
import korobkin.nikita.user_profile_service.controller.UserProfileController;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileAvatarRequest;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.security.UserPrincipal;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private JwtProperties jwtProperties;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void getMyProfile_success() throws Exception {
        given(userProfileService.getUserProfile(any(UUID.class)))
                .willReturn(getUserProfileResponse(userId));

        mockMvc.perform(get("/api/profiles/me")
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, "test@mail.com"))))
                .andExpect(status().isOk())
                .andExpectAll(expectUserProfile(userId));
    }

    @Test
    void fillMyProfile_success() throws Exception {
        given(userProfileService.fillUserProfile(any(UUID.class), any(UpdateUserProfileRequest.class)))
                .willReturn(getUserProfileResponse(userId));

        UpdateUserProfileRequest request = getUserProfileRequest();

        mockMvc.perform(post("/api/profiles")
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, "test@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, "test@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getUserProfileRequest())))
                .andExpect(status().isOk())
                .andExpectAll(expectUserProfile(userId));
    }

    @Test
    void updateProfileAvatar_success() throws Exception {
        given(userProfileService.updateUserProfileAvatar(any(UUID.class), any(UpdateUserProfileAvatarRequest.class)))
                .willReturn(getUserProfileResponse(userId));

        UpdateUserProfileAvatarRequest request = new UpdateUserProfileAvatarRequest();
        request.setAvatarUrl("http://avatarUrl");

        mockMvc.perform(patch("/api/profiles/me/avatar")
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, "test@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpectAll(expectUserProfile(userId));
    }

    @Test
    void searchProfilesBySkills_success() throws Exception {
        Page<UserProfileResponse> page = new PageImpl<>(List.of(getUserProfileResponse(userId)));
        given(userProfileService.findBySkills(any(Set.class), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/profiles")
                        .param("skills", "skill1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$.content[0].nickname").value("nickname"))
                .andExpect(jsonPath("$.content[0].firstName").value("firstName"))
                .andExpect(jsonPath("$.content[0].lastName").value("lastName"))
                .andExpect(jsonPath("$.content[0].bio").value("bio"))
                .andExpect(jsonPath("$.content[0].avatarUrl").value("http://avatarUrl"))
                .andExpect(jsonPath("$.content[0].skills[0]").value("skill1"))
                .andExpect(jsonPath("$.content[0].links.github").value("github"));
    }

    @Test
    void deleteMyProfile_success() throws Exception {
        willDoNothing().given(userProfileService).deleteUserProfile(any(UUID.class));

        mockMvc.perform(delete("/api/profiles/me")
                        .with(TestSecurityUtils.userPrincipal(new UserPrincipal(userId, "test@mail.com"))))
                .andExpect(status().isNoContent());
    }

    private UserProfileResponse getUserProfileResponse(UUID userId) {
        return new UserProfileResponse(
                userId,
                "nickname",
                "firstName",
                "lastName",
                "bio",
                "http://avatarUrl",
                Set.of("skill1"),
                Map.of("github", "github"));
    }

    private UpdateUserProfileRequest getUserProfileRequest() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setNickname("nickname");
        request.setFirstName("firstName");
        request.setLastName("lastName");
        request.setBio("bio");
        request.setAvatarUrl("http://avatarUrl");
        request.setSkills(Set.of("skill1"));
        request.setLinks(Map.of("github", "github"));
        return request;
    }

    private ResultMatcher[] expectUserProfile(UUID userId) {
        return new ResultMatcher[]{
                jsonPath("$.userId").value(userId.toString()),
                jsonPath("$.nickname").value("nickname"),
                jsonPath("$.firstName").value("firstName"),
                jsonPath("$.lastName").value("lastName"),
                jsonPath("$.bio").value("bio"),
                jsonPath("$.avatarUrl").value("http://avatarUrl"),
                jsonPath("$.skills[0]").value("skill1"),
                jsonPath("$.links.github").value("github")
        };
    }
}

