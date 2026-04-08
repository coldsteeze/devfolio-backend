package korobkin.nikita.skill_verification_service.gateway.github;

import korobkin.nikita.skill_verification_service.config.GitHubTokenProperties;
import korobkin.nikita.skill_verification_service.gateway.github.dto.GitHubContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class GitHubClient {

    private final WebClient webClient;

    public GitHubClient(GitHubTokenProperties properties) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization", "Bearer " + properties.getToken())
                .build();
    }

    public List<GitHubContentResponse> getContents(String owner, String repo, String path) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                .retrieve()
                .bodyToFlux(GitHubContentResponse.class)
                .collectList()
                .doOnNext(list -> log.debug("Loaded contents for {}/{} path={}, items={}", owner, repo, path, list.size()))
                .block();
    }

    public Mono<String> getFileContentAsync(String owner, String repo, String path) {
        String branch = "main";
        String rawUrl = String.format(
                "https://raw.githubusercontent.com/%s/%s/%s/%s",
                owner, repo, branch, path
        );

        return webClient.get()
                .uri(rawUrl)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(c -> log.debug("Loaded file {} (len={})", path, c != null ? c.length() : 0))
                .doOnError(e -> log.warn("Error loading file {}: {}", path, e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }
}
