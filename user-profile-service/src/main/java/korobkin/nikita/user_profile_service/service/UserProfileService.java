package korobkin.nikita.user_profile_service.service;

import korobkin.nikita.events.UserCreatedEvent;

public interface UserProfileService {

    void createUserProfile(UserCreatedEvent event);
}
