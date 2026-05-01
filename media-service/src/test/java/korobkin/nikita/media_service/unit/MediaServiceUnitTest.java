package korobkin.nikita.media_service.unit;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import korobkin.nikita.media_service.config.MediaProperties;
import korobkin.nikita.media_service.config.MinioProperties;
import korobkin.nikita.media_service.dto.MediaResponse;
import korobkin.nikita.media_service.exception.*;
import korobkin.nikita.media_service.service.impl.MediaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceUnitTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @Mock
    private MediaProperties mediaProperties;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private MediaServiceImpl mediaService;

    private final String bucket = "test-bucket";
    private final String baseUrl = "http://localhost:9000";

    @Test
    void upload_shouldUploadFileSuccessfully() throws Exception {
        when(minioProperties.getBucket()).thenReturn(bucket);
        when(minioProperties.getPublicUrl()).thenReturn(baseUrl);

        when(mediaProperties.getAllowedTypes())
                .thenReturn(List.of("image/png", "image/jpeg"));
        when(mediaProperties.getMaxFileSize())
                .thenReturn(DataSize.ofMegabytes(5));

        when(file.getOriginalFilename()).thenReturn("image.png");
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(1024L);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1]));
        when(file.isEmpty()).thenReturn(false);

        MediaResponse response = mediaService.upload(file, "avatars");

        assertNotNull(response);
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void upload_shouldThrow_whenFileEmpty() {
        when(file.isEmpty()).thenReturn(true);

        assertThrows(FileEmptyException.class,
                () -> mediaService.upload(file, "avatars"));
    }

    @Test
    void upload_shouldThrow_whenInvalidType() {
        when(mediaProperties.getAllowedTypes())
                .thenReturn(List.of("image/png"));

        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");

        assertThrows(InvalidFileTypeException.class,
                () -> mediaService.upload(file, "avatars"));
    }

    @Test
    void upload_shouldThrow_whenFileTooLarge() {
        when(mediaProperties.getAllowedTypes())
                .thenReturn(List.of("image/png"));
        when(mediaProperties.getMaxFileSize())
                .thenReturn(DataSize.ofMegabytes(1));

        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(DataSize.ofMegabytes(10).toBytes());

        assertThrows(FileTooLargeException.class,
                () -> mediaService.upload(file, "avatars"));
    }

    @Test
    void upload_shouldThrow_whenInvalidFolder() {
        when(mediaProperties.getAllowedTypes())
                .thenReturn(List.of("image/png"));
        when(mediaProperties.getMaxFileSize())
                .thenReturn(DataSize.ofMegabytes(5));

        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(100L);

        assertThrows(InvalidFolderException.class,
                () -> mediaService.upload(file, "!!!invalid!!!"));
    }

    @Test
    void upload_shouldThrow_whenMinioFails() throws Exception {
        when(minioProperties.getBucket()).thenReturn(bucket);

        when(mediaProperties.getAllowedTypes())
                .thenReturn(List.of("image/png"));
        when(mediaProperties.getMaxFileSize())
                .thenReturn(DataSize.ofMegabytes(5));

        when(file.getOriginalFilename()).thenReturn("image.png");
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(100L);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1]));
        when(file.isEmpty()).thenReturn(false);

        doAnswer(invocation -> {
            throw new RuntimeException("Minio error");
        }).when(minioClient).putObject(any(PutObjectArgs.class));

        assertThrows(FileUploadException.class,
                () -> mediaService.upload(file, "avatars"));
    }

    @Test
    void delete_shouldRemoveObjectSuccessfully() throws Exception {
        when(minioProperties.getBucket()).thenReturn(bucket);
        when(minioProperties.getUrl()).thenReturn(baseUrl);

        String url = baseUrl + "/" + bucket + "/avatars/test.png";

        mediaService.delete(url);

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void delete_shouldThrow_whenUrlNull() {
        assertThrows(InvalidFileUrlException.class,
                () -> mediaService.delete(null));
    }

    @Test
    void delete_shouldThrow_whenUrlNotFromBucket() {
        when(minioProperties.getBucket()).thenReturn(bucket);
        when(minioProperties.getUrl()).thenReturn(baseUrl);

        assertThrows(InvalidFileUrlException.class,
                () -> mediaService.delete("http://evil.com/file.png"));
    }

    @Test
    void delete_shouldThrow_whenMinioFails() throws Exception {
        when(minioProperties.getBucket()).thenReturn(bucket);
        when(minioProperties.getUrl()).thenReturn(baseUrl);

        String url = baseUrl + "/" + bucket + "/avatars/test.png";

        doThrow(new RuntimeException())
                .when(minioClient)
                .removeObject(any(RemoveObjectArgs.class));

        assertThrows(FileDeleteException.class,
                () -> mediaService.delete(url));
    }
}