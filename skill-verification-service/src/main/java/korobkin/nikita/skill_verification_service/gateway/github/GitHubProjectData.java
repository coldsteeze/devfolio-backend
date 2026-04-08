package korobkin.nikita.skill_verification_service.gateway.github;

import korobkin.nikita.skill_verification_service.gateway.github.dto.GitHubContentResponse;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
public class GitHubProjectData implements ProjectData {

    private final GitHubClient client;
    private final String owner;
    private final String repo;
    private List<String> rootDirsCache;

    private final Map<String, Mono<Optional<String>>> cache = new ConcurrentHashMap<>();

    @Override
    public Optional<String> getFileContent(String path) {
        return cache.computeIfAbsent(path, p -> {
            log.debug("Loading file from GitHub: {}", p);

            return client.getFileContentAsync(owner, repo, p)
                    .map(Optional::of)
                    .defaultIfEmpty(Optional.empty());
        }).block();
    }

    @Override
    public boolean fileExists(String path) {
        return getFileContent(path).isPresent();
    }

    @Override
    public List<String> getRootDirectories() {
        if (rootDirsCache != null) return rootDirsCache;

        log.debug("Loading root directories for {}/{}", owner, repo);

        rootDirsCache = client.getContents(owner, repo, "")
                .stream()
                .filter(c -> "dir".equalsIgnoreCase(c.getType()))
                .map(GitHubContentResponse::getName)
                .toList();

        log.debug("Root directories loaded: {}", rootDirsCache);

        return rootDirsCache;
    }
}
