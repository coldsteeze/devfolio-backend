package korobkin.nikita.user_profile_service.service.impl;

import jakarta.transaction.Transactional;
import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public void createUserProfile(UserCreatedEvent userCreatedEvent) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userCreatedEvent.userId());
        userProfileRepository.save(userProfile);
        log.info("UserProfile with user_id:{} save in DB", userProfile.getUserId());
    }
}
