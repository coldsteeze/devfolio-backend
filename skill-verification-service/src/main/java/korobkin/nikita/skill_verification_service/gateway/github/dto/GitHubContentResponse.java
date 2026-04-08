package korobkin.nikita.skill_verification_service.gateway.github.dto;

import lombok.Getter;

@Getter
public class GitHubContentResponse {

    private String name;
    private String path;
    private String type;
    private String content;
}