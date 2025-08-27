package korobkin.nikita.auth_service.mapper;

import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(RegisterRequest registerRequest);
}
