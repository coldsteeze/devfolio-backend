package korobkin.nikita.project_service.client;

import korobkin.nikita.project_service.dto.response.skill.SkillResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "skill-service",
        url = "${services.skill-service.url}"
)
public interface SkillClient {

    @GetMapping("/api/skills/{skillId}")
    SkillResponse getSkillById(@PathVariable("skillId") UUID skillId);
}
