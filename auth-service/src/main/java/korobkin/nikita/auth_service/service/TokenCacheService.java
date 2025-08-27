package korobkin.nikita.auth_service.service;

import java.util.UUID;

public interface TokenCacheService {

    void saveRefreshToken(UUID userId, String refreshToken, long ttlDays);

    String getRefreshToken(UUID userId);

    void deleteRefreshToken(UUID userId);
}
