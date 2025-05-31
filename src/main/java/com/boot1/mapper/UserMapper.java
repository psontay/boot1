package com.boot1.mapper;

import com.boot1.Entities.User;
import com.boot1.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper( componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

}
