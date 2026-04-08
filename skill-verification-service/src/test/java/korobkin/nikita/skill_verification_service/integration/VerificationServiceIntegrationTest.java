package korobkin.nikita.skill_verification_service.integration;

import korobkin.nikita.events.ProjectSkillVerificationCompletedEvent;
import korobkin.nikita.events.ProjectSkillVerificationRequestedEvent;
import korobkin.nikita.events.skill.SkillCategory;
import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.config.KafkaTopicProperties;
import korobkin.nikita.skill_verification_service.entity.VerifiedSkill;
import korobkin.nikita.skill_verification_service.gateway.RepositoryGateway;
import korobkin.nikita.skill_verification_service.kafka.consumer.VerificationConsumer;
import korobkin.nikita.skill_verification_service.kafka.producer.VerificationProducer;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.repository.VerifiedSkillRepository;
import korobkin.nikita.skill_verification_service.rule.RuleRegistry;
import korobkin.nikita.skill_verification_service.rule.SkillVerificationRule;
import korobkin.nikita.skill_verification_service.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class VerificationServiceIntegrationTest {

    @Autowired
    private VerificationService service;

    @Autowired
    private VerifiedSkillRepository repository;

    @MockitoBean
    private RepositoryGateway gateway;

    @MockitoBean
    private RuleRegistry registry;

    @MockitoBean
    private VerificationProducer producer;

    @MockitoBean
    private VerificationConsumer consumer;

    @MockitoBean
    private KafkaTopicProperties kafkaTopicProperties;

    @Test
    void shouldVerifySkills_andSaveToDatabase_andSendEvent() {
        UUID projectId = UUID.randomUUID();

        SkillShortInfo skill1 = new SkillShortInfo(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Java",
                SkillCategory.LANGUAGE
        );

        SkillShortInfo skill2 = new SkillShortInfo(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Spring",
                SkillCategory.FRAMEWORK
        );

        ProjectSkillVerificationRequestedEvent event =
                new ProjectSkillVerificationRequestedEvent(
                        projectId,
                        "https://github.com/test/repo",
                        List.of(skill1, skill2)
                );

        ProjectData data = mock(ProjectData.class);
        when(gateway.load(any())).thenReturn(data);

        SkillVerificationRule rule = mock(SkillVerificationRule.class);
        when(registry.find(any())).thenReturn(Optional.of(rule));
        when(rule.verify(any(), any())).thenReturn(true);

        when(kafkaTopicProperties.getProjectSkillVerificationCompleted())
                .thenReturn("test-topic");

        service.verify(event);

        List<VerifiedSkill> saved = repository.findAll();
        assertEquals(2, saved.size());

        assertTrue(saved.stream().allMatch(vs -> vs.getProjectId().equals(projectId)));

        assertTrue(saved.stream().anyMatch(vs -> vs.getSkillName().equals("Java")));

        verify(producer).sendSync(
                eq("test-topic"),
                any(ProjectSkillVerificationCompletedEvent.class)
        );
    }

    @Test
    void shouldNotSaveAnything_ifNoRuleFound() {
        UUID projectId = UUID.randomUUID();

        SkillShortInfo skill = new SkillShortInfo(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Unknown",
                SkillCategory.TOOL
        );

        ProjectSkillVerificationRequestedEvent event =
                new ProjectSkillVerificationRequestedEvent(
                        projectId,
                        "url",
                        List.of(skill)
                );

        ProjectData data = mock(ProjectData.class);
        when(gateway.load(any())).thenReturn(data);

        when(registry.find(any())).thenReturn(Optional.empty());

        when(kafkaTopicProperties.getProjectSkillVerificationCompleted())
                .thenReturn("test-topic");

        service.verify(event);

        assertTrue(repository.findAll().isEmpty());

        verify(producer).sendSync(any(), any());
    }
}
