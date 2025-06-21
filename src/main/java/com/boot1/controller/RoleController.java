package com.boot1.controller;

import com.boot1.Entities.Role;
import com.boot1.dto.request.RoleRequest;
import com.boot1.dto.response.ApiResponse;
import com.boot1.dto.response.RoleResponse;
import com.boot1.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults( makeFinal = true , level = AccessLevel.PRIVATE)
public class RoleController {
    RoleService roleService;
    @PostMapping("/create")
    public ApiResponse<RoleResponse> create(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .code(1)
                .msg("<Create Role Success>")
                .result(roleService.create(roleRequest))
                          .build();
    }
    @GetMapping("/list")
    public ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .code(1)
                .msg("<Get All Role Success>")
                          .result(roleService.getAll())
                          .build();
    }
    @PostMapping("/findByName")
    public ApiResponse<RoleResponse> findByName(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .code(1)
                .msg("<Find Role By Name Success>")
                .result(roleService.findByName(roleRequest))
                .build();
    }
    @GetMapping("/existsByName/{name}")
    public ApiResponse<Boolean> existByName(@PathVariable String name) {
        return ApiResponse.<Boolean>builder()
                .code(1)
                .result(roleService.existsByName(name))
                .msg("<Exist Role By Name Success>")
                          .build();
    }
    @GetMapping("/findByDescription/{des}")
    public ApiResponse<RoleResponse> findByDescription(@PathVariable String des) {
        return ApiResponse.<RoleResponse>builder()
                .code(1)
                .msg("<Find Role By Description Success>")
                .result(roleService.findByDescription(des))
                .build();
    }
    @Transactional
    @DeleteMapping("/delete/{name}")
    public ApiResponse<String> deleteByName(@PathVariable String name) {
        roleService.deleteByName(name);
        return ApiResponse.<String>builder()
                .code(1)
                .msg("<Delete Role Success>")
                .result("<Delete Role Success>")
                          .build();
    }
    @GetMapping("/keyword")
    public ApiResponse<List<RoleResponse>> findByKeyword(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<List<RoleResponse>>builder()
                .code(1)
                .msg("<Find By Keyword Success>")
                .result(roleService.findAllBynameContainningIgnoreCase(roleRequest.getName()))
                          .build();
    }
    @Transactional
    @PutMapping("/update")
    public ApiResponse<RoleResponse> update(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .code(1)
                .msg("<Update Role Success>")
                          .result(roleService.updateFromRequest(roleRequest))
                          .build();
    }
}
