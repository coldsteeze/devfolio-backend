package korobkin.nikita.user_profile_service.service;

import korobkin.nikita.user_profile_service.dto.request.UpdateCareerRequest;
import korobkin.nikita.user_profile_service.dto.response.CareerResponse;

import java.util.UUID;

public interface CareerService {

    CareerResponse getCareer(UUID userId);

    CareerResponse updateCareer(UUID uuid, UpdateCareerRequest request);
}
