package korobkin.nikita.skill_verification_service.gateway;

import korobkin.nikita.skill_verification_service.model.ProjectData;

public interface RepositoryGateway {

    ProjectData load(String githubUrl);
}
