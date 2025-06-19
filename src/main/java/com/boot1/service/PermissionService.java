package com.boot1.service;

import com.boot1.Entities.Permission;
import com.boot1.dto.request.PermissionRequest;
import com.boot1.dto.response.PermissionResponse;
import com.boot1.exception.ApiException;
import com.boot1.exception.ErrorCode;
import com.boot1.mapper.PermissionMapper;
import com.boot1.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EnableMethodSecurity
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse createPermission(PermissionRequest request) {
        if ( permissionRepository.existsByName(request.getPermissionName())) {
            throw new ApiException( ErrorCode.PERMISSION_EXISTS );
        }
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }
    @PreAuthorize( "hasRole('ADMIN')")
    public List<PermissionResponse> getPermissions() {
        log.info("method getPermissions");
        return permissionRepository.findAll()
                                   .stream()
                                   .map(permissionMapper::toPermissionResponse)
                                   .collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse findByName(String permissionName) {
        log.info("method findPermissionByName");
        return permissionRepository.findByName(permissionName)
                .map(permissionMapper::toPermissionResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.PERMISSION_NOT_FOUND));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public boolean existsByName ( String permissionName) {
        log.info("method existsPermissionByPermissionName");
        return permissionRepository.existsByName(permissionName);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse findByDescription(String description) {
        log.info("method findByDescription");
        return permissionRepository.findByDescription(description)
                .map(permissionMapper::toPermissionResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.PERMISSION_NOT_FOUND));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void updatePermissionByName ( String permissionName, PermissionRequest request) {
        log.info("method updatePermissionByName");
        Permission permission = permissionRepository
                .findByName(permissionName)
                .orElseThrow(() -> new ApiException(ErrorCode.PERMISSION_NOT_FOUND));
        permissionMapper.updatePermissionFromRequest(request, permission);
        permissionRepository.save(permission);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByName ( String permissionName) {
        log.info("method deletePermissionByName");
        Permission permission = permissionRepository.findByName(permissionName)
                                                            .orElseThrow(() -> new ApiException(ErrorCode.PERMISSION_NOT_FOUND));
        permissionRepository.delete(permissionName);
    }
    public List<PermissionResponse> searchByKeyword(String keyword) {
        log.info("method searchByKeyword");
        return permissionRepository.findAllByNameContainingIgnoreCase(keyword)
                .stream()
                                   .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }
}
