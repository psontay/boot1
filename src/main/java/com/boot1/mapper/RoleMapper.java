package com.boot1.mapper;

import com.boot1.Entities.Role;
import com.boot1.dto.request.RoleRequest;
import com.boot1.dto.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper( componentModel = "spring")
public interface RoleMapper {
    @Mapping ( source = "name" , target = "name")
    @Mapping( target = "permissions" , ignore = true)
    Role toRole (RoleRequest roleRequest);
    @Mapping( source = "name" , target = "name")
    RoleResponse toRoleResponse (Role role);
    RoleResponse updateRoleFromRequest( RoleRequest roleRequest , @MappingTarget Role role);
}
