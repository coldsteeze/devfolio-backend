package korobkin.nikita.skill_verification_service.rule;

import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FileContentRule implements SkillVerificationRule {

    private final String skillName;
    private final List<String> fileNames;
    private final List<String> keywords;

    @Override
    public boolean supports(SkillShortInfo skill) {
        return skill.name().equalsIgnoreCase(skillName);
    }

    @Override
    public boolean verify(SkillShortInfo skill, ProjectData data) {
        if (check("", data)) return true;

        return data.getRootDirectories()
                .stream()
                .anyMatch(dir -> check(dir, data));
    }

    private boolean check(String prefix, ProjectData data) {
        String base = prefix.isEmpty() ? "" : prefix + "/";

        for (String file : fileNames) {
            Optional<String> content = data.getFileContent(base + file);

            if (content.isPresent()) {
                String lower = content.get().toLowerCase();

                for (String keyword : keywords) {
                    if (lower.contains(keyword.toLowerCase())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
