package korobkin.nikita.media_service.service;

import korobkin.nikita.media_service.dto.MediaResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    MediaResponse upload(MultipartFile file, String folder);

    void delete(String url);
}
