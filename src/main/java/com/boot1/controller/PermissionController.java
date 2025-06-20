package com.boot1.controller;

import com.boot1.Entities.Permission;
import com.boot1.dto.request.PermissionRequest;
import com.boot1.dto.response.ApiResponse;
import com.boot1.dto.response.PermissionResponse;
import com.boot1.mapper.PermissionMapper;
import com.boot1.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/permissions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionController {
    @Autowired
    PermissionService permissionService;
    @PostMapping("/create")
    ApiResponse<PermissionResponse> createPermission(@RequestBody @Valid PermissionRequest permissionRequest) {
        return ApiResponse.<PermissionResponse>builder()
                          .code(1)
                          .msg("<Create Permission Successfully>")
                          .result(permissionService.createPermission(permissionRequest))
                          .build();
    }
    @GetMapping("/list")
    ApiResponse<List<PermissionResponse>> listPermission() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("<Username>" + authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<PermissionResponse>>builder()
                          .code(1)
                          .msg("<List Permission Successfully>")
                          .result(permissionService.getPermissions())
                          .build();
    }
    @GetMapping("/findByName/{name}")
    ApiResponse<PermissionResponse> findPermissionByName(@PathVariable String name) {
        return ApiResponse.<PermissionResponse>builder()
                          .code(1)
                          .msg("<Find Permission Successfully>")
                          .result(permissionService.findByName(name))
                          .build();
    }
    @GetMapping("/existsByName/{name}")
    ApiResponse<Boolean> existsPermissionByName(@PathVariable String name) {
        return ApiResponse.<Boolean>builder()
                .result(permissionService.existsByName(name))
                .code(1)
                .msg("<Exists Permission Successfully>")
                          .build();
    }
    @GetMapping("/findByDescription/{description}")
    ApiResponse<PermissionResponse> findPermissionByDescription(@PathVariable String description) {
        return ApiResponse.<PermissionResponse>builder()
                          .result(permissionService.findByDescription(description))
                          .code(1)
                          .msg("<Find Permission By Description Successfully>")
                          .build();
    }
    @PutMapping("/update/{name}")
    ApiResponse<PermissionResponse> updatePermission(@PathVariable String name,
                                    @RequestBody @Valid PermissionRequest permissionRequest) {
        return ApiResponse.<PermissionResponse>builder()
                          .result(permissionService.updatePermissionByName(name , permissionRequest))
                          .code(1)
                          .msg("<Update Permission Successfully>")
                          .build();
    }
    @Transactional
    @DeleteMapping("delete/{name}")
    ApiResponse<String> deletePermissionByName(@PathVariable String name) {
        permissionService.deletePermissionByName(name);
        return ApiResponse.<String>builder()
                .code(1)
                .msg("<Delete Permission Successfully>")
                .result("OK")
                          .build();
    }
    @GetMapping("keyword/{keyword}")
    ApiResponse<List<PermissionResponse>> findByKeyword( @PathVariable String keyword) {
        return ApiResponse.<List<PermissionResponse>>builder()
                .code(1)
                .msg("<Find Permission By Keyword Successfully>")
                .result(permissionService.searchByKeyword(keyword)).build();
    }
    @GetMapping("/test-scope")
    public void testScope() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: ");
        auth.getAuthorities().forEach(a -> System.out.println(a.getAuthority()));
    }

}
