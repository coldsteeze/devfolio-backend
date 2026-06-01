package korobkin.nikita.skill_verification_service.integration;

import korobkin.nikita.events.ProjectSkillVerificationRequestedEvent;
import korobkin.nikita.events.skill.SkillCategory;
import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.config.KafkaTopicProperties;
import korobkin.nikita.skill_verification_service.entity.OutboxEvent;
import korobkin.nikita.skill_verification_service.entity.VerifiedSkill;
import korobkin.nikita.skill_verification_service.gateway.RepositoryGateway;
import korobkin.nikita.skill_verification_service.kafka.consumer.VerificationConsumer;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.repository.OutboxEventRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class VerificationServiceIntegrationTest {

    @Autowired
    private VerificationService service;

    @Autowired
    private VerifiedSkillRepository repository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @MockitoBean
    private RepositoryGateway gateway;

    @MockitoBean
    private RuleRegistry registry;

    @MockitoBean
    private VerificationConsumer consumer;

    @MockitoBean
    private KafkaTopicProperties kafkaTopicProperties;

    @Test
    void shouldVerifySkills_andSaveToDatabase_andSaveOutboxEvent() {
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
                        UUID.randomUUID(),
                        projectId,
                        "https://github.com/test/repo",
                        List.of(skill1, skill2)
                );

        ProjectData data = mock(ProjectData.class);
        when(gateway.load(any())).thenReturn(data);

        SkillVerificationRule rule = mock(SkillVerificationRule.class);
        when(registry.find(any())).thenReturn(Optional.of(rule));
        when(rule.verify(any(), any())).thenReturn(true);

        service.verify(event);

        List<VerifiedSkill> saved = repository.findAll();
        assertEquals(2, saved.size());

        assertTrue(saved.stream().allMatch(vs -> vs.getProjectId().equals(projectId)));

        List<OutboxEvent> outboxEvents = outboxEventRepository.findAll();
        assertEquals(1, outboxEvents.size());

        OutboxEvent outboxEvent = outboxEvents.get(0);

        assertEquals("PROJECT", outboxEvent.getAggregateType());
        assertEquals(projectId, outboxEvent.getAggregateId());
        assertEquals("project.skill.verification.completed", outboxEvent.getEventType());

        assertNotNull(outboxEvent.getPayload());
    }

    @Test
    void shouldNotSaveAnything_ifNoRuleFound_andStillSaveOutboxEventWithEmptyResult() {
        UUID projectId = UUID.randomUUID();

        SkillShortInfo skill = new SkillShortInfo(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Unknown",
                SkillCategory.TOOL
        );

        ProjectSkillVerificationRequestedEvent event =
                new ProjectSkillVerificationRequestedEvent(
                        UUID.randomUUID(),
                        projectId,
                        "url",
                        List.of(skill)
                );

        ProjectData data = mock(ProjectData.class);
        when(gateway.load(any())).thenReturn(data);

        when(registry.find(any())).thenReturn(Optional.empty());

        service.verify(event);

        assertTrue(repository.findAll().isEmpty());

        List<OutboxEvent> outboxEvents = outboxEventRepository.findAll();
        assertEquals(1, outboxEvents.size());

        OutboxEvent outboxEvent = outboxEvents.get(0);

        assertEquals("PROJECT", outboxEvent.getAggregateType());
        assertEquals("project.skill.verification.completed", outboxEvent.getEventType());
    }
}
