package korobkin.nikita.skill_verification_service.gateway.github;

import korobkin.nikita.skill_verification_service.gateway.github.dto.GitHubContentResponse;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class GitHubProjectData implements ProjectData {

    private final GitHubClient client;
    private final String owner;
    private final String repo;

    private final Map<String, String> cache = new HashMap<>();

    @Override
    public Optional<String> getFileContent(String path) {
        // Если есть в кеше — возвращаем
        if (cache.containsKey(path)) {
            return Optional.of(cache.get(path));
        }

        // Получаем содержимое файла напрямую через GitHubClient
        Optional<String> contentOpt = client.getFileContent(owner, repo, path);

        contentOpt.ifPresent(c -> cache.put(path, c));

        return contentOpt;
    }

    @Override
    public boolean fileExists(String path) {
        return getFileContent(path).isPresent();
    }

    @Override
    public List<String> findFilesByName(String name) {
        // Получаем содержимое корня
        List<GitHubContentResponse> contents = client.getContents(owner, repo, "");

        return contents.stream()
                .map(GitHubContentResponse::getName)
                .filter(n -> n.equals(name))
                .toList();
    }

    public List<String> getAllFiles() {
        List<String> files = new ArrayList<>();
        collectFiles("", files);
        return files;
    }

    private void collectFiles(String path, List<String> files) {
        List<GitHubContentResponse> contents = client.getContents(owner, repo, path);

        for (GitHubContentResponse content : contents) {
            // проверяем тип через content.getType()
            String type = content.getType(); // должно быть "file" или "dir"
            String fullPath = path.isEmpty() ? content.getName() : path + "/" + content.getName();

            if ("file".equalsIgnoreCase(type)) {
                files.add(fullPath);
            } else if ("dir".equalsIgnoreCase(type)) {
                collectFiles(fullPath, files);
            }
        }
    }

    /** Получаем первые директории (микросервисы) */
    public List<String> listDirectories() {
        Set<String> dirs = new HashSet<>();
        for (String path : getAllFiles()) {
            int slash = path.indexOf('/');
            if (slash > 0) {
                dirs.add(path.substring(0, slash));
            }
        }
        return new ArrayList<>(dirs);
    }
}
