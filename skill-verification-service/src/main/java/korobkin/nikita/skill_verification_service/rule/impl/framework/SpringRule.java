package korobkin.nikita.skill_verification_service.rule.impl.framework;

import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.rule.SkillVerificationRule;
import org.springframework.stereotype.Component;

@Component
public class SpringRule implements SkillVerificationRule {

    public boolean supports(SkillShortInfo skill) {
        return skill.name().equalsIgnoreCase("Spring");
    }

    public boolean verify(SkillShortInfo skill, ProjectData data) {
        return data.getFileContent("pom.xml")
                .map(c -> c.toLowerCase().contains("spring"))
                .orElse(false);
    }
}
