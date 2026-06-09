package korobkin.nikita.user_profile_service.repository;

import korobkin.nikita.user_profile_service.dto.response.ProfileFeedResponse;
import korobkin.nikita.user_profile_service.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Boolean existsByNicknameAndUserIdNot(String nickname, UUID userId);

    @Query("""
            SELECT new korobkin.nikita.user_profile_service.dto.response.ProfileFeedResponse(
                p.userId,
                p.nickname,
                CONCAT(p.firstName, ' ', p.lastName),
                p.avatarUrl,
                p.userType,
                SUBSTRING(p.bio, 1, 150),
                p.createdAt
            )
            FROM UserProfile p
            ORDER BY p.createdAt DESC
            """)
    Page<ProfileFeedResponse> findFeedProfiles(Pageable pageable);

    @Query("""
                SELECT new korobkin.nikita.user_profile_service.dto.response.ProfileFeedResponse(
                    p.userId,
                    p.nickname,
                    CONCAT(p.firstName, ' ', p.lastName),
                    p.avatarUrl,
                    p.userType,
                    SUBSTRING(COALESCE(p.bio, ''), 1, 150),
                    p.createdAt
                )
                FROM UserProfile p
                WHERE
                    LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE CONCAT('%', LOWER(:search), '%')
                    OR LOWER(p.nickname) LIKE CONCAT('%', LOWER(:search), '%')
                ORDER BY p.createdAt DESC
            """)
    Page<ProfileFeedResponse> searchFeedProfiles(@Param("search") String search, Pageable pageable);
}
