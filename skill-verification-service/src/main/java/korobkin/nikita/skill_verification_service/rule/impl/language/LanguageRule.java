package korobkin.nikita.skill_verification_service.rule.impl.language;

import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.rule.SkillVerificationRule;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LanguageRule implements SkillVerificationRule {

    private final String skillName;
    private final List<String> indicators;

    @Override
    public boolean supports(SkillShortInfo skill) {
        return skill.name().equalsIgnoreCase(skillName);
    }

    @Override
    public boolean verify(SkillShortInfo skill, ProjectData data) {
        if (check("", data)) return true;

        return data.getRootDirectories().stream()
                .anyMatch(dir -> check(dir, data));
    }

    private boolean check(String prefix, ProjectData data) {
        String base = prefix.isEmpty() ? "" : prefix + "/";

        for (String indicator : indicators) {
            if (data.fileExists(base + indicator)) {
                return true;
            }
        }

        return false;
    }
}
