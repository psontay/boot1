package com.boot1.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boot1.Entities.Permission;
import com.boot1.Entities.Role;
import com.boot1.dto.request.RoleRequest;
import com.boot1.dto.response.RoleResponse;
import com.boot1.exception.ApiException;
import com.boot1.exception.ErrorCode;
import com.boot1.mapper.RoleMapper;
import com.boot1.repository.PermissionRepository;
import com.boot1.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

//    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse create(RoleRequest roleRequest) {
        log.info("<Create Role Method> {}", roleRequest.getName());
        if (roleRepository.existsByName(roleRequest.getName())) {
            throw new ApiException(ErrorCode.ROLE_EXISTS);
        }
        Set<String> permissionNames = roleRequest.getPermissions();
        List<Permission> permissions = permissionRepository.findAllByNameIn(permissionNames);
        if (permissions.isEmpty()) {
            throw new ApiException(ErrorCode.PERMISSION_IS_EMPTY);
        }
        Role role = roleMapper.toRole(roleRequest);
        role.setPermissions(new HashSet<>(permissions));
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse findByName(String roleName) {
        log.info("<Find Role Method> {}", roleName);
        return roleRepository
                .findByName(roleName)
                .map(roleMapper::toRoleResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean existsByName(String roleName) {
        log.info("<Exists Role Method> {}", roleName);
        return roleRepository.existsByName(roleName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse findByDescription(String des) {
        log.info("<Find Role By Description Method> {}", des);
        return roleRepository
                .findByDescription(des)
                .map(roleMapper::toRoleResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByName(String roleName) {
        log.info("<Delete Role Method> {}", roleName);
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
        roleRepository.deleteByName(roleName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> findAllBynameContainningIgnoreCase(String keyWord) {
        log.info("<Find Role By Keyword Method> {}", keyWord);
        return roleRepository.findAllByNameContainingIgnoreCase(keyWord).stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse updateFromRequest(RoleRequest roleRequest) {
        log.info("<Update Role Method> {}", roleRequest.getName());
        Role role = roleRepository
                .findByName(roleRequest.getName())
                .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
        roleMapper.updateRoleFromRequest(roleRequest, role);
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse addPermissionsToRole(String roleName, List<String> permissionNames) {
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
        Set<String> names = new HashSet<>(permissionNames);
        List<Permission> permissions = permissionRepository.findAllByNameIn(names);
        if (permissions.size() != permissionNames.size()) {
            List<String> foundNames =
                    permissions.stream().map(Permission::getName).collect(Collectors.toList());
            List<String> missing = permissionNames.stream()
                    .filter(p -> !foundNames.contains(p))
                    .collect(Collectors.toList());
            throw new ApiException(ErrorCode.PERMISSION_NOT_FOUND, "Missing Permission : " + missing);
        }
        role.getPermissions().addAll(permissions);
        Role save = roleRepository.save(role);
        return roleMapper.toRoleResponse(save);
    }
}
