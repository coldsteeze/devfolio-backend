package korobkin.nikita.user_profile_service.service.impl;

import korobkin.nikita.user_profile_service.dto.request.UpdateCareerRequest;
import korobkin.nikita.user_profile_service.dto.response.CareerResponse;
import korobkin.nikita.user_profile_service.entity.CareerEntry;
import korobkin.nikita.user_profile_service.exception.ErrorCode;
import korobkin.nikita.user_profile_service.exception.UserProfileNotFoundException;
import korobkin.nikita.user_profile_service.mapper.CareerMapper;
import korobkin.nikita.user_profile_service.repository.CareerEntryRepository;
import korobkin.nikita.user_profile_service.repository.UserProfileRepository;
import korobkin.nikita.user_profile_service.service.CareerService;
import korobkin.nikita.user_profile_service.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerServiceImpl implements CareerService {

    private final CareerEntryRepository careerEntryRepository;
    private final UserProfileRepository userProfileRepository;
    private final CareerMapper careerMapper;
    private final OutboxEventService outboxEventService;

    @Override
    @Transactional(readOnly = true)
    public CareerResponse getCareer(UUID userId) {
        log.info("Fetching career for userId={}", userId);

        validateUserExists(userId);

        List<CareerEntry> entries = careerEntryRepository.findByUserId(userId);

        log.info("Found {} career entries for userId={}", entries.size(), userId);

        return careerMapper.toResponse(entries);
    }

    @Override
    @Transactional
    public CareerResponse updateCareer(UUID userId, UpdateCareerRequest request) {
        log.info("Updating career for userId={} with {} items",
                userId,
                request.items().size()
        );

        validateUserExists(userId);

        careerEntryRepository.deleteByUserId(userId);

        log.info("Deleted old career entries for userId={}", userId);

        List<CareerEntry> entities = request.items().stream()
                .map(careerMapper::toEntity)
                .peek(e -> e.setUserId(userId))
                .toList();

        List<CareerEntry> saved = careerEntryRepository.saveAll(entities);

        log.info("Saved {} career entries for userId={}", saved.size(), userId);

        outboxEventService.saveEvent(
                "USER-PROFILE",
                userId,
                "user-profile-career-updated",
                careerMapper.toUpdatedEvent(userId, saved)
        );

        return careerMapper.toResponse(saved);
    }

    private void validateUserExists(UUID userId) {
        if (!userProfileRepository.existsById(userId)) {
            log.warn("UserProfile not found for userId={}", userId);
            throw new UserProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }
}
