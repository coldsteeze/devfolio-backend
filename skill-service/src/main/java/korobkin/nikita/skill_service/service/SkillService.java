package korobkin.nikita.skill_service.service;

import korobkin.nikita.skill_service.dto.request.BulkSkillRequest;
import korobkin.nikita.skill_service.dto.request.CreateSkillRequest;
import korobkin.nikita.skill_service.dto.request.SkillFilterRequest;
import korobkin.nikita.skill_service.dto.request.UpdateSkillRequest;
import korobkin.nikita.skill_service.dto.response.PagedResponse;
import korobkin.nikita.skill_service.dto.response.SkillResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


public interface SkillService {

    PagedResponse<SkillResponse> findSkills(SkillFilterRequest request, Pageable pageable);

    SkillResponse createSkill(CreateSkillRequest createSkillRequest);

    SkillResponse findSkill(UUID skillId);

    List<SkillResponse> findBulkSkills(BulkSkillRequest request);

    SkillResponse updateSkill(UUID skillId, UpdateSkillRequest request);

    void deactivateSkill(UUID skillId);
}
