package korobkin.nikita.skill_verification_service.rule.impl.platform;

import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.rule.SkillVerificationRule;
import org.springframework.stereotype.Component;

@Component
public class DockerRule implements SkillVerificationRule {

    @Override
    public boolean supports(SkillShortInfo skill) {
        return skill.name().equalsIgnoreCase("Docker");
    }

    @Override
    public boolean verify(SkillShortInfo skill, ProjectData data) {

        if (data.fileExists("Dockerfile")
                || data.fileExists("docker-compose.yml")) {
            return true;
        }

        return data.getRootDirectories().stream()
                .anyMatch(dir ->
                        data.fileExists(dir + "/Dockerfile") ||
                                data.fileExists(dir + "/docker-compose.yml")
                );
    }
}
