package korobkin.nikita.user_profile_service.repository;

import korobkin.nikita.user_profile_service.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Boolean existsByNicknameAndUserIdNot(String nickname, UUID userId);

    @Query("SELECT DISTINCT u FROM UserProfile u JOIN u.skills s WHERE s IN :skills")
    Page<UserProfile> findBySkillsIn(@Param("skills") Set<String> skills, Pageable pageable);
}
