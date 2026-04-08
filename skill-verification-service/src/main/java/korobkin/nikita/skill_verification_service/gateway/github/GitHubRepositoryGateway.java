package korobkin.nikita.skill_verification_service.gateway.github;

import korobkin.nikita.skill_verification_service.gateway.RepositoryGateway;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitHubRepositoryGateway implements RepositoryGateway {

    private final GitHubClient client;

    @Override
    public ProjectData load(String githubUrl) {
        RepoInfo repo = parse(githubUrl);

        return new GitHubProjectData(
                client,
                repo.owner(),
                repo.repo()
        );
    }

    private RepoInfo parse(String githubUrl) {
        String[] parts = githubUrl.replace("https://github.com/", "").split("/");

        return new RepoInfo(parts[0], parts[1]);
    }
}
