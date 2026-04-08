package korobkin.nikita.skill_verification_service.unit;

import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.skill_verification_service.model.ProjectData;
import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import korobkin.nikita.skill_verification_service.rule.impl.language.*;
import korobkin.nikita.skill_verification_service.rule.impl.platform.DockerRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleUnitTest {

    @ParameterizedTest
    @CsvSource({
            "Java, pom.xml, true",
            "Java, build.gradle, true",
            "Java, random.txt, false",
            "Python, requirements.txt, true",
            "Python, pyproject.toml, true",
            "Python, random.txt, false",
            "Go, go.mod, true",
            "Go, random.txt, false",
            "TypeScript, tsconfig.json, true",
            "TypeScript, random.txt, false",
            "JavaScript, package.json, true",
            "JavaScript, random.txt, false"
    })
    void languageRule_verify(String lang, String file, boolean expected) {
        LanguageRule rule = switch (lang) {
            case "Java" -> new JavaRule();
            case "Python" -> new PythonRule();
            case "Go" -> new GoRule();
            case "TypeScript" -> new TypeScriptRule();
            case "JavaScript" -> new JavaScriptRule();
            default -> throw new IllegalArgumentException("Unknown language: " + lang);
        };

        ProjectData data = mock(ProjectData.class);
        when(data.getRootDirectories()).thenReturn(List.of());
        when(data.fileExists(file)).thenReturn(true);

        assertEquals(expected, rule.verify(mock(SkillShortInfo.class), data));
    }

    @ParameterizedTest
    @MethodSource("korobkin.nikita.skill_verification_service.unit.RuleTestData#provideFileContentRules")
    void fileContentRule_verify(FileContentRule rule, String fileName, String keyword) {
        ProjectData data = mock(ProjectData.class);
        when(data.getRootDirectories()).thenReturn(List.of());

        when(data.getFileContent(fileName)).thenReturn(Optional.of("some content " + keyword));

        assertTrue(rule.verify(mock(SkillShortInfo.class), data));
    }

    @Test
    void dockerRule_verifyDockerfile() {
        DockerRule rule = new DockerRule();
        ProjectData data = mock(ProjectData.class);
        when(data.getRootDirectories()).thenReturn(List.of());
        when(data.fileExists("Dockerfile")).thenReturn(true);

        assertTrue(rule.verify(mock(SkillShortInfo.class), data));
    }

    @Test
    void dockerRule_verifyDockerCompose() {
        DockerRule rule = new DockerRule();
        ProjectData data = mock(ProjectData.class);
        when(data.getRootDirectories()).thenReturn(List.of());
        when(data.fileExists("docker-compose.yml")).thenReturn(true);

        assertTrue(rule.verify(mock(SkillShortInfo.class), data));
    }

    @Test
    void dockerRule_verifyNothing() {
        DockerRule rule = new DockerRule();
        ProjectData data = mock(ProjectData.class);
        when(data.getRootDirectories()).thenReturn(List.of());
        when(data.fileExists("Dockerfile")).thenReturn(false);
        when(data.fileExists("docker-compose.yml")).thenReturn(false);

        assertFalse(rule.verify(mock(SkillShortInfo.class), data));
    }
}
