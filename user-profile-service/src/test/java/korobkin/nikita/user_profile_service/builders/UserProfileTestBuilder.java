package korobkin.nikita.user_profile_service.builders;

import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.fixtures.UserProfileFixtures;

import java.util.*;

public class UserProfileTestBuilder {

    private UUID userId = UUID.randomUUID();
    private String nickname = UserProfileFixtures.DEFAULT_NICKNAME;
    private Set<String> skills = new HashSet<>();
    private final Map<String, String> links = new HashMap<>();

    public UserProfileTestBuilder withUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UserProfileTestBuilder withNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public UserProfileTestBuilder withSkill(String skill) {
        this.skills.add(skill);
        return this;
    }

    public UserProfileTestBuilder withSkills(Set<String> skills) {
        this.skills = new HashSet<>(skills);
        return this;
    }

    public UserProfile build() {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setNickname(nickname);
        profile.setSkills(skills);
        profile.setLinks(links);
        return profile;
    }
}
