package korobkin.nikita.skill_verification_service.unit;

import korobkin.nikita.events.ProjectSkillVerificationRequestedEvent;
import korobkin.nikita.events.skill.SkillCategory;
import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.config.KafkaTopicProperties;
import korobkin.nikita.skill_verification_service.entity.VerifiedSkill;
import korobkin.nikita.skill_verification_service.gateway.RepositoryGateway;
import korobkin.nikita.skill_verification_service.kafka.producer.VerificationProducer;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.repository.VerifiedSkillRepository;
import korobkin.nikita.skill_verification_service.rule.RuleRegistry;
import korobkin.nikita.skill_verification_service.rule.SkillVerificationRule;
import korobkin.nikita.skill_verification_service.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceUnitTest {

    @InjectMocks
    private VerificationService service;

    @Mock
    private VerifiedSkillRepository repository;

    @Mock
    private RepositoryGateway gateway;

    @Mock
    private RuleRegistry registry;

    @Mock
    private VerificationProducer producer;

    @Mock
    private KafkaTopicProperties kafkaTopicProperties;

    @Test
    void shouldSaveVerifiedSkillsAndSendEvent() {
        UUID projectId = UUID.randomUUID();
        SkillShortInfo skill = new SkillShortInfo(UUID.randomUUID(), UUID.randomUUID(), "Java", SkillCategory.LANGUAGE);
        ProjectSkillVerificationRequestedEvent event = new ProjectSkillVerificationRequestedEvent(projectId, "url", List.of(skill));

        ProjectData data = mock(ProjectData.class);
        when(gateway.load(any())).thenReturn(data);

        SkillVerificationRule rule = mock(SkillVerificationRule.class);
        when(registry.find(any())).thenReturn(Optional.of(rule));
        when(rule.verify(any(), any())).thenReturn(true);
        when(kafkaTopicProperties.getProjectSkillVerificationCompleted()).thenReturn("topic");

        service.verify(event);

        verify(repository).deleteByProjectId(projectId);
        verify(repository).saveAll(any());
        verify(producer).sendSync(eq("topic"), any());
    }

    @Test
    void shouldNotSaveIfNoRule() {
        UUID projectId = UUID.randomUUID();
        SkillShortInfo skill = new SkillShortInfo(UUID.randomUUID(), UUID.randomUUID(), "Unknown", SkillCategory.TOOL);
        ProjectSkillVerificationRequestedEvent event = new ProjectSkillVerificationRequestedEvent(projectId, "url", List.of(skill));

        ProjectData data = mock(ProjectData.class);
        when(gateway.load(any())).thenReturn(data);
        when(registry.find(any())).thenReturn(Optional.empty());
        when(kafkaTopicProperties.getProjectSkillVerificationCompleted()).thenReturn("topic");

        service.verify(event);

        verify(repository, never()).saveAll(any());
        verify(producer).sendSync(eq("topic"), any());
    }

    @Test
    void shouldSaveOnlyVerifiedSkills() {
        UUID projectId = UUID.randomUUID();
        SkillShortInfo skill1 = new SkillShortInfo(UUID.randomUUID(), UUID.randomUUID(), "Java", SkillCategory.LANGUAGE);
        SkillShortInfo skill2 = new SkillShortInfo(UUID.randomUUID(), UUID.randomUUID(), "Unknown", SkillCategory.TOOL);
        ProjectSkillVerificationRequestedEvent event = new ProjectSkillVerificationRequestedEvent(projectId, "url", List.of(skill1, skill2));

        ProjectData data = mock(ProjectData.class);
        when(gateway.load(any())).thenReturn(data);

        SkillVerificationRule rule = mock(SkillVerificationRule.class);
        when(registry.find(skill1)).thenReturn(Optional.of(rule));
        when(registry.find(skill2)).thenReturn(Optional.empty());
        when(rule.verify(any(), any())).thenReturn(true);

        when(kafkaTopicProperties.getProjectSkillVerificationCompleted()).thenReturn("topic");

        service.verify(event);

        verify(repository).saveAll(argThat(iterable -> {
            List<VerifiedSkill> list = StreamSupport.stream(iterable.spliterator(), false).toList();
            return list.size() == 1 && list.get(0).getSkillName().equals("Java");
        }));

        verify(producer).sendSync(eq("topic"), any());
    }
}
