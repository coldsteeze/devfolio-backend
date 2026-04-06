package korobkin.nikita.skill_verification_service.rule.impl.platform;

import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.rule.SkillVerificationRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaRule implements SkillVerificationRule {

    @Override
    public boolean supports(SkillShortInfo skill) {
        return skill.name().equalsIgnoreCase("Kafka");
    }

    @Override
    public boolean verify(SkillShortInfo skill, ProjectData data) {
        List<String> services = data.listDirectories();

        for (String servicePath : services) {
            boolean javaKafka = data.getFileContent(servicePath + "/pom.xml")
                    .map(c -> c.toLowerCase().contains("kafka"))
                    .orElse(false);
            if (javaKafka) return true;

            boolean pythonKafka = data.getFileContent(servicePath + "/requirements.txt")
                    .map(c -> c.toLowerCase().contains("kafka"))
                    .orElse(false);
            if (pythonKafka) return true;

            boolean jsKafka = data.getFileContent(servicePath + "/package.json")
                    .map(c -> c.toLowerCase().contains("kafka"))
                    .orElse(false);
            if (jsKafka) return true;
        }

        return false;
    }
}
