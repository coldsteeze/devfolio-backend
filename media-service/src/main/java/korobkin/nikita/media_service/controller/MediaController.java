package korobkin.nikita.media_service.controller;

import korobkin.nikita.media_service.docs.MediaControllerDocs;
import korobkin.nikita.media_service.dto.DeleteMediaRequest;
import korobkin.nikita.media_service.dto.MediaResponse;
import korobkin.nikita.media_service.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController implements MediaControllerDocs {

    private final MediaService mediaService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("folder") String folder
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mediaService.upload(file, folder));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody DeleteMediaRequest request) {
        mediaService.delete(request.url());
        return ResponseEntity.noContent().build();
    }
}
