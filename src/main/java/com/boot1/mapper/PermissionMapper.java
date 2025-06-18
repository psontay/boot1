package com.boot1.mapper;

import com.boot1.Entities.Permission;
import com.boot1.dto.request.PermissionRequest;
import com.boot1.dto.response.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper( componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse(Permission permission);
    void updatePermissionFromRequest(PermissionRequest request, @MappingTarget Permission permission);
}
