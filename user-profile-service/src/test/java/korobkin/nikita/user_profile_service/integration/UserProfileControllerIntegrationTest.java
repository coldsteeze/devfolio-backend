package korobkin.nikita.user_profile_service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.fixtures.UserProfileFixtures;
import korobkin.nikita.user_profile_service.fixtures.UserProfileRequestFixtures;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "services.media-service.url=http://localhost:${wiremock.server.port}"
})
public class UserProfileControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private UUID userA;
    private String authenticationEmail;

    @BeforeEach
    void setUp() {
        userA = UUID.randomUUID();
        authenticationEmail = "test@mail.com";
    }

    @Test
    void getMyProfile_shouldReturnProfile() throws Exception {
        UserProfile profile = UserProfileFixtures.builder().withUserId(userA).build();
        userProfileRepository.save(profile);

        mockMvc.perform(get("/api/profiles/me")
                        .with(auth(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(profile.getUserId().toString()))
                .andExpect(jsonPath("$.nickname").value(UserProfileFixtures.DEFAULT_NICKNAME));
    }

    @Test
    void fillMyProfile_shouldCreateProfile() throws Exception {
        UpdateUserProfileRequest request = UserProfileRequestFixtures.updateUserProfileRequest();
        UserProfile emptyProfile = UserProfileFixtures.builder().withUserId(userA).build();
        userProfileRepository.save(emptyProfile);

        mockMvc.perform(post("/api/profiles")
                        .with(auth(userA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname")
                        .value(UserProfileRequestFixtures.DEFAULT_NICKNAME))
                .andExpect(jsonPath("$.firstName")
                        .value(UserProfileRequestFixtures.DEFAULT_FIRST_NAME))
                .andExpect(jsonPath("$.lastName")
                        .value(UserProfileRequestFixtures.DEFAULT_LAST_NAME))
                .andExpect(jsonPath("$.bio")
                        .value(UserProfileRequestFixtures.DEFAULT_BIO))
                .andExpect(jsonPath("$.links.github")
                        .value("https://github.com"));
    }

    @Test
    void deleteMyProfile_shouldDeleteProfile() throws Exception {
        UserProfile profile = UserProfileFixtures.builder().withUserId(userA).build();
        userProfileRepository.save(profile);

        mockMvc.perform(delete("/api/profiles/me")
                        .with(auth(userA)))
                .andExpect(status().isNoContent());

        assertThat(userProfileRepository.findById(profile.getUserId())).isEmpty();
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    private RequestPostProcessor auth(UUID userId) {
        return authentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(userId, authenticationEmail),
                null,
                Collections.emptyList()
        ));
    }
}
