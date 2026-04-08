package korobkin.nikita.skill_verification_service.model;

import java.util.List;
import java.util.Optional;

public interface ProjectData {

    boolean fileExists(String path);

    Optional<String> getFileContent(String path);

    List<String> getRootDirectories();
}
