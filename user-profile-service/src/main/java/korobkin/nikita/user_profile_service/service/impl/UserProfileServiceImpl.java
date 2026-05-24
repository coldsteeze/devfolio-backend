package korobkin.nikita.user_profile_service.service.impl;

import feign.FeignException;
import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.user_profile_service.client.MediaClient;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.MediaResponse;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import korobkin.nikita.user_profile_service.exception.ErrorCode;
import korobkin.nikita.user_profile_service.exception.NicknameAlreadyTakenException;
import korobkin.nikita.user_profile_service.exception.UserProfileAvatarNotFoundException;
import korobkin.nikita.user_profile_service.exception.UserProfileNotFoundException;
import korobkin.nikita.user_profile_service.exception.media.MediaErrorMapper;
import korobkin.nikita.user_profile_service.kafka.producer.UserProfileDeletedEventProducer;
import korobkin.nikita.user_profile_service.kafka.producer.UserProfileUpdatedEventProducer;
import korobkin.nikita.user_profile_service.mapper.UserProfileMapper;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserProfileDeletedEventProducer userProfileDeletedEventProducer;
    private final UserProfileUpdatedEventProducer userProfileUpdatedEventProducer;
    private final MediaClient mediaClient;
    private final MediaErrorMapper mediaErrorMapper;

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

        userProfileUpdatedEventProducer.sendUserProfileUpdated(
                userProfileMapper.toUpdatedEvent(userProfile)
        );

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
    public void deleteUserProfile(UUID id) {
        userProfileRepository.deleteById(id);
        log.info("UserProfile with id: {} delete in DB", id);
        userProfileDeletedEventProducer.sendUserDeleted(new UserDeletedEvent(id));
    }

    @Override
    @Transactional
    public MediaResponse uploadUserProfileAvatar(MultipartFile file, UserPrincipal principal) {
        log.info("Uploading user profile avatar photo: userId={}, fileName={}",
                principal.userId(),
                file != null ? file.getOriginalFilename() : "null"
        );

        UserProfile userProfile = findProfileById(principal.userId());

        MediaResponse response = safeUpload(file);

        String old = userProfile.getAvatarUrl();
        userProfile.setAvatarUrl(response.url());

        if (old != null) {
            try {
                safeDelete(old);
            } catch (Exception ex) {
                log.warn("Failed to delete old avatar: {}", ex.getMessage());
            }
        }

        return response;
    }

    @Override
    @Transactional
    public void deleteUserProfileAvatar(UserPrincipal principal) {
        UserProfile userProfile = findProfileById(principal.userId());

        if (userProfile.getAvatarUrl() == null) {
            throw new UserProfileAvatarNotFoundException(ErrorCode.PROFILE_AVATAR_NOT_FOUND);
        }

        safeDelete(userProfile.getAvatarUrl());

        userProfile.setAvatarUrl(null);
    }

    private UserProfile findAndValidateProfile(UUID id, UpdateUserProfileRequest request) {
        UserProfile userProfile = findProfileById(id);

        if (userProfileRepository.existsByNicknameAndUserIdNot(request.getNickname(), id)) {
            throw new NicknameAlreadyTakenException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        userProfileMapper.updateEntityFromDto(request, userProfile);

        return userProfile;
    }

    private UserProfile findProfileById(UUID id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));
    }

    private MediaResponse safeUpload(MultipartFile file) {
        try {
            return mediaClient.upload(file, "user-profile/avatars");
        } catch (FeignException ex) {
            log.error("Media upload failed: status={}, body={}",
                    ex.status(), ex.contentUTF8(), ex);
            throw mediaErrorMapper.map(ex);
        }
    }

    private void safeDelete(String url) {
        try {
            mediaClient.delete(url);
        } catch (FeignException ex) {
            log.error("Media delete failed: status={}, body={}",
                    ex.status(), ex.contentUTF8(), ex);
            throw mediaErrorMapper.map(ex);
        }
    }
}

