package korobkin.nikita.skill_verification_service.rule;

import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.model.ProjectData;

public interface SkillVerificationRule {

    boolean supports(SkillShortInfo skill);

    boolean verify(SkillShortInfo skill, ProjectData data);
}