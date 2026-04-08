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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final RepositoryGateway gateway;
    private final RuleRegistry registry;
    private final VerifiedSkillRepository repository;
    private final VerificationProducer verificationProducer;
    private final KafkaTopicProperties kafkaTopicProperties;

    @Transactional
    public void verify(ProjectSkillVerificationRequestedEvent event) {
        log.info("Start verification: projectId={}, skills={}",
                event.projectId(), event.skills().size());

        ProjectData data = gateway.load(event.githubUrl());

        repository.deleteByProjectId(event.projectId());

        List<VerifiedSkill> result = event.skills().parallelStream()
                .map(skill -> registry.find(skill)
                        .filter(rule -> rule.verify(skill, data))
                        .map(rule -> map(skill, event))
                )
                .flatMap(Optional::stream)
                .toList();

        repository.saveAll(result);

        log.info("Verification completed: projectId={}, verifiedSkills={}",
                event.projectId(), result.size());

        verificationProducer.sendSync(
                kafkaTopicProperties.getProjectSkillVerificationCompleted(),
                new ProjectSkillVerificationCompletedEvent(
                        event.projectId(),
                        result.stream()
                                .map(vs -> new SkillVerificationResult(vs.getProjectSkillId(), true))
                                .toList()
                )
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
