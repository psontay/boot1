package com.boot1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.boot1.Entities.Role;
import com.boot1.dto.request.RoleRequest;
import com.boot1.dto.response.RoleResponse;

@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public interface RoleMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);

    @Mapping(source = "name", target = "name")
    RoleResponse toRoleResponse(Role role);

    @Mapping(target = "permissions", ignore = true)
    void updateRoleFromRequest(RoleRequest roleRequest, @MappingTarget Role role);
}
