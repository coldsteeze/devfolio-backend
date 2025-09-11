package korobkin.nikita.user_profile_service.repository;

import korobkin.nikita.user_profile_service.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Boolean existsByNickname(String nickname);
}
