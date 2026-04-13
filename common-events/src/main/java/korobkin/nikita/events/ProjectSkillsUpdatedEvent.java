package korobkin.nikita.events;

import java.util.List;
import java.util.UUID;

public record ProjectSkillsUpdatedEvent(
        UUID projectId,
        List<ProjectSkillDto> skills
) {}
