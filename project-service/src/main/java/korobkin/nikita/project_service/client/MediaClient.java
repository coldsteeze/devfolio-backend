package korobkin.nikita.project_service.client;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import korobkin.nikita.project_service.client.config.FeignMultipartConfig;
import korobkin.nikita.project_service.dto.request.DeleteMediaRequest;
import korobkin.nikita.project_service.dto.response.media.MediaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "media-service",
        url = "${services.media-service.url}",
        configuration = FeignMultipartConfig.class
)
public interface MediaClient {

    @PostMapping(value = "/api/media/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    MediaResponse upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("folder") String folder
    );

    @DeleteMapping("/api/media")
    void delete(@RequestBody DeleteMediaRequest request);
}
