package com.boot1.mapper;

import com.boot1.Entities.Permission;
import com.boot1.dto.request.PermissionRequest;
import com.boot1.dto.response.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper( componentModel = "spring")
public interface PermissionMapper {
    @Mapping(source = "permissionName" , target = "name")
    Permission toPermission(PermissionRequest permissionRequest);
    @Mapping(source = "name", target = "permissionName")
    PermissionResponse toPermissionResponse(Permission permission);
    void updatePermissionFromRequest(PermissionRequest request, @MappingTarget Permission permission);
}
