package korobkin.nikita.user_profile_service.service.impl;

import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileAvatarRequest;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.exception.NicknameAlreadyTakenException;
import korobkin.nikita.user_profile_service.exception.UserProfileNotFoundException;
import korobkin.nikita.user_profile_service.kafka.producer.UserEventProducer;
import korobkin.nikita.user_profile_service.mapper.UserProfileMapper;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserEventProducer  userEventProducer;

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
        UserProfile userProfile = findProfileById(id);

        return userProfileMapper.toDto(userProfile);
    }

    @Override
    @Transactional
    public UserProfileResponse fillUserProfile(UUID id, UpdateUserProfileRequest request) {
        UserProfile userProfile = findAndValidateProfile(id, request);
        userProfile.setUpdatedAt(LocalDateTime.now());
        userProfileRepository.save(userProfile);
        log.info("UserProfile with id: {} data has been filled in DB", userProfile.getUserId());

        return userProfileMapper.toDto(userProfile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(UUID id, UpdateUserProfileRequest request) {
        UserProfile userProfile = findAndValidateProfile(id, request);
        userProfile.setUpdatedAt(LocalDateTime.now());
        userProfileRepository.save(userProfile);
        log.info("UserProfile with id: {} data has been updated in DB", userProfile.getUserId());

        return userProfileMapper.toDto(userProfile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfileAvatar(UUID id, UpdateUserProfileAvatarRequest request) {
        UserProfile userProfile = findProfileById(id);
        userProfile.setUpdatedAt(LocalDateTime.now());
        userProfile.setAvatarUrl(request.getAvatarUrl());
        userProfileRepository.save(userProfile);
        log.info("UserProfile with id: {} updated avatar to {}", id, request.getAvatarUrl());

        return userProfileMapper.toDto(userProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> findBySkills(Set<String> skills, Pageable pageable) {
        Page<UserProfile> profiles;

        if (skills.isEmpty()) {
            profiles = userProfileRepository.findAll(pageable);
        } else {
            profiles = userProfileRepository.findBySkillsIn(skills, pageable);
        }

        return profiles.map(userProfileMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteUserProfile(UUID id) {
        userProfileRepository.deleteById(id);
        log.info("UserProfile with id: {} delete in DB", id);
        userEventProducer.sendUserDeleted(new UserDeletedEvent(id));
    }

    private UserProfile findAndValidateProfile(UUID id, UpdateUserProfileRequest request) {
        UserProfile userProfile = findProfileById(id);

        if (userProfileRepository.existsByNicknameAndUserIdNot(request.getNickname(), id)) {
            throw new NicknameAlreadyTakenException("Nickname already exists");
        }

        userProfileMapper.updateEntityFromDto(request, userProfile);

        return userProfile;
    }

    private UserProfile findProfileById(UUID id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException("User with this id not found"));
    }
}

