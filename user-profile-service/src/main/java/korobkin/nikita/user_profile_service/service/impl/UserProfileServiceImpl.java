package korobkin.nikita.user_profile_service.service.impl;

import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.exception.NicknameAlreadyTakenException;
import korobkin.nikita.user_profile_service.exception.UserProfileNotFoundException;
import korobkin.nikita.user_profile_service.mapper.UserProfileMapper;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public void createUserEmptyProfile(UserCreatedEvent userCreatedEvent) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userCreatedEvent.userId());
        userProfileRepository.save(userProfile);
        log.info("UserProfile with user_id:{} save in DB", userProfile.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException("User with this id not found"));

        return userProfileMapper.toDto(userProfile);
    }

    @Override
    @Transactional
    public UserProfileResponse fillUserProfile(UUID id, UpdateUserProfileRequest request) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException("User with this id not found"));

        if (userProfileRepository.existsByNickname(request.getNickname())) {
            throw new NicknameAlreadyTakenException("Nickname already exists");
        }

        userProfileMapper.updateEntityFromDto(request, userProfile);
        userProfile.setUpdatedAt(LocalDateTime.now());
        userProfileRepository.save(userProfile);
        log.info("UserProfile with id: {} data has been filled in DB", userProfile.getUserId());

        return userProfileMapper.toDto(userProfile);
    }
}
