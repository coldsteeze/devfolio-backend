package korobkin.nikita.events;

import java.util.List;
import java.util.UUID;

public record ProjectSkillsUpdatedEvent(
        UUID eventId,
        UUID projectId,
        List<ProjectSkillDto> skills
) {}
