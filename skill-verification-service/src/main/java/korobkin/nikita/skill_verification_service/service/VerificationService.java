package korobkin.nikita.skill_verification_service.service;

import korobkin.nikita.events.ProjectSkillVerificationCompletedEvent;
import korobkin.nikita.events.ProjectSkillVerificationRequestedEvent;
import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.events.skill.SkillVerificationResult;
import korobkin.nikita.skill_verification_service.config.KafkaTopicProperties;
import korobkin.nikita.skill_verification_service.entity.VerifiedSkill;
import korobkin.nikita.skill_verification_service.gateway.RepositoryGateway;
import korobkin.nikita.skill_verification_service.kafka.producer.VerificationProducer;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.repository.VerifiedSkillRepository;
import korobkin.nikita.skill_verification_service.rule.RuleRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final RepositoryGateway gateway;
    private final RuleRegistry registry;
    private final VerifiedSkillRepository repository;
    private final VerificationProducer verificationProducer;
    private final KafkaTopicProperties kafkaTopicProperties;

    @Transactional
    public void verify(ProjectSkillVerificationRequestedEvent event) {

        ProjectData data = gateway.load(event.githubUrl());

        repository.deleteByProjectId(event.projectId());

        List<VerifiedSkill> result = new ArrayList<>();

        for (SkillShortInfo skill : event.skills()) {

            registry.find(skill)
                    .filter(rule -> rule.verify(skill, data))
                    .ifPresent(rule -> result.add(map(skill, event)));
        }

        repository.saveAll(result);

        List<SkillVerificationResult> skillVerificationResultList = result.stream()
                .map(vs -> new SkillVerificationResult(vs.getProjectSkillId(), true))
                .toList();

        verificationProducer.sendSync(
                kafkaTopicProperties.getProjectSkillVerificationCompleted(),
                new ProjectSkillVerificationCompletedEvent(event.projectId(), skillVerificationResultList)
        );
    }

    private VerifiedSkill map(SkillShortInfo skill,
                              ProjectSkillVerificationRequestedEvent event) {

        VerifiedSkill vs = new VerifiedSkill();

        vs.setProjectId(event.projectId());
        vs.setProjectSkillId(skill.projectSkillId());
        vs.setSkillId(skill.skillId());
        vs.setSkillName(skill.name());
        vs.setVerifiedAt(Instant.now());

        return vs;
    }
}
