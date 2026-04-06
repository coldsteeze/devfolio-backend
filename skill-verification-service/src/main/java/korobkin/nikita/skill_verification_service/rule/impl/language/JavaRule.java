package korobkin.nikita.skill_verification_service.rule.impl.language;

import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.rule.SkillVerificationRule;
import org.springframework.stereotype.Component;

@Component
public class JavaRule implements SkillVerificationRule {

    public boolean supports(SkillShortInfo skill) {
        return skill.name().equalsIgnoreCase("Java");
    }

    public boolean verify(SkillShortInfo skill, ProjectData data) {
        return data.fileExists("pom.xml")
                || data.fileExists("build.gradle");
    }
}
