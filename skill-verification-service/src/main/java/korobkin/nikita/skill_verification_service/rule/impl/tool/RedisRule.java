package korobkin.nikita.skill_verification_service.rule.impl.tool;

import korobkin.nikita.skill_verification_service.rule.CommonFiles;
import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisRule extends FileContentRule {

    public RedisRule() {
        super(
                "Redis",
                CommonFiles.BACKEND,
                List.of(
                        "redis",
                        "spring.redis",
                        "lettuce",
                        "jedis",
                        "ioredis",
                        "redis://"
                )
        );
    }
}
