package korobkin.nikita.user_profile_service.exception.media;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import korobkin.nikita.user_profile_service.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MediaErrorMapper {

    private final ObjectMapper objectMapper;

    public RuntimeException map(FeignException ex) {
        try {
            ApiError error = objectMapper.readValue(ex.contentUTF8(), ApiError.class);

            return switch (error.getCode()) {

                case "INVALID_FILE_TYPE" ->
                        new MediaInvalidTypeException();

                case "FILE_TOO_LARGE" ->
                        new MediaFileTooLargeException();

                case "INVALID_FILE_URL" ->
                        new MediaInvalidUrlException();

                default ->
                        new MediaUploadException();
            };

        } catch (Exception e) {
            return new MediaUploadException();
        }
    }
}
