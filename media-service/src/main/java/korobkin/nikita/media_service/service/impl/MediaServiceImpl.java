package korobkin.nikita.media_service.service.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import korobkin.nikita.media_service.config.MediaProperties;
import korobkin.nikita.media_service.config.MinioProperties;
import korobkin.nikita.media_service.dto.MediaResponse;
import korobkin.nikita.media_service.exception.*;
import korobkin.nikita.media_service.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final MediaProperties mediaProperties;

    @Override
    public MediaResponse upload(MultipartFile file, String folder) {
        validate(file, folder);

        try {
            String extension = getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;

            String objectName = folder + "/" + fileName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return new MediaResponse(minioProperties.getPublicUrl() + "/" +
                    minioProperties.getBucket() + "/" +
                    objectName);

        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new FileUploadException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public void delete(String url) {
        validateUrl(url);

        String internalUrl = toInternalUrl(url);

        String objectName = extractObjectName(internalUrl);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to delete file", e);
            throw new FileDeleteException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new InvalidFileUrlException(ErrorCode.INVALID_FILE_URL);
        }
    }

    private String extractObjectName(String url) {
        String base = minioProperties.getUrl() + "/" + minioProperties.getBucket() + "/";

        if (!url.startsWith(base)) {
            throw new InvalidFileUrlException(ErrorCode.INVALID_FILE_URL);
        }

        return url.substring(base.length());
    }

    private void validate(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new FileEmptyException(ErrorCode.FILE_EMPTY);
        }

        if (!mediaProperties.getAllowedTypes().contains(file.getContentType())) {
            throw new InvalidFileTypeException(ErrorCode.INVALID_FILE_TYPE);
        }

        if (file.getSize() > mediaProperties.getMaxFileSize().toBytes()) {
            throw new FileTooLargeException(ErrorCode.FILE_TOO_LARGE);
        }

        if (folder == null || folder.isBlank() || !folder.matches("^[a-zA-Z0-9/_-]+$")) {
            throw new InvalidFolderException(ErrorCode.INVALID_FOLDER);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf("."));
    }

    private String toInternalUrl(String publicUrl) {
        return publicUrl.replace(
                minioProperties.getPublicUrl(),
                minioProperties.getUrl()
        );
    }
}
