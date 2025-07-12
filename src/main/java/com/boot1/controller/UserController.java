package com.boot1.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.boot1.dto.request.UserCreationRequest;
import com.boot1.dto.request.UserUpdateRequest;
import com.boot1.dto.response.ApiResponse;
import com.boot1.dto.response.UserResponse;
import com.boot1.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1)
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping("/findById/{id}")
    public ApiResponse<UserResponse> findById(@PathVariable String id) {
        return ApiResponse.<UserResponse>builder()
                .code(1)
                .result(userService.findUserById(id))
                .build();
    }

    @GetMapping("/list")
    public ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .code(1)
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile() {
        return ApiResponse.<UserResponse>builder()
                .code(1)
                .result(userService.getMyProfile())
                .build();
    }

    @GetMapping("/findByUsername/{username}")
    public ApiResponse<UserResponse> findByUsername(@PathVariable String username) {
        return ApiResponse.<UserResponse>builder()
                .code(1)
                .result(userService.findUserByUsername(username))
                .build();
    }

    @GetMapping("/findByEmail/{email}")
    public ApiResponse<UserResponse> findByEmail(@PathVariable String email) {
        return ApiResponse.<UserResponse>builder()
                .code(1)
                .result(userService.findUserByEmail(email))
                .build();
    }

    @GetMapping("/findByFirstName/{firstName}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> findByFirstName(@PathVariable String firstName) {

        List<UserResponse> result = userService.findByFirstName(firstName);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder().code(1).result(result).build());
    }

    @GetMapping("/findByFirstAndLastName/{firstName}/{lastName}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> findByFirstAndLastName(
            @PathVariable String firstName, @PathVariable String lastName) {
        List<UserResponse> result = userService.findByFirstAndLastName(firstName, lastName);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder().code(1).result(result).build());
    }

    @GetMapping("/findByLastName/{lastName}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> findByLastName(@PathVariable String lastName) {
        List<UserResponse> result = userService.findByLastName(lastName);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder().code(1).result(result).build());
    }

    @PutMapping("/update/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable String id, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1)
                .result(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder().code(1).msg("Deleted successfully").build();
    }
}
