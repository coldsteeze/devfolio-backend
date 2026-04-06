package korobkin.nikita.skill_verification_service.gateway.github;

import korobkin.nikita.skill_verification_service.config.GitHubTokenProperties;
import korobkin.nikita.skill_verification_service.gateway.github.dto.GitHubContentResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Component
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
                .block();
    }

    public String getFile(String downloadUrl) {
        return webClient.get()
                .uri(downloadUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public Optional<String> getFileContent(String owner, String repo, String path) {
        try {
            String branch = "main";
            String rawUrl = String.format(
                    "https://raw.githubusercontent.com/%s/%s/%s/%s",
                    owner, repo, branch, path
            );

            String content = webClient.get()
                    .uri(rawUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("[GitHubClient] Содержимое файла " + path + " загружено, длина: " + (content != null ? content.length() : 0));
            return Optional.ofNullable(content);

        } catch (Exception e) {
            System.err.println("[GitHubClient] Ошибка при получении файла: " + e.getMessage());
            return Optional.empty();
        }
    }
}
