package com.boot1.controller;

import com.boot1.Entities.User;
import com.boot1.dto.request.UserCreationRequest;
import com.boot1.dto.request.UserUpdateRequest;
import com.boot1.dto.response.ApiResponse;
import com.boot1.dto.response.UserResponse;
import com.boot1.mapper.UserMapper;
import com.boot1.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/create")
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setCode(1);
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }
    @GetMapping("findById/{id}")
    UserResponse findById(@PathVariable String id) {
        return userService.findUserById(id);
    }
    @GetMapping("/list")
    ApiResponse<List<UserResponse>> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username : {}" , authentication.getName());
        authentication.getAuthorities().forEach( grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }
    @GetMapping("/findByFirstName/{firstName}")
    List<User> findByFirstName(@PathVariable String firstName) {
        return userService.findUsersByFirstName(firstName);
    }
    @GetMapping("/findByFirstNameAndLastName")
    List<User> findByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        return userService.findUserByFirstNameAndLastName(firstName, lastName);
    }
    @GetMapping("findByUsername/{username}")
    Optional<User> findByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }
    @GetMapping("/findByLastName/{lastName}")
    List<User> findByLastName(@PathVariable String lastName) {
        return userService.findUserByLastName(lastName);
    }
    @GetMapping("/findByEmail/{email}")
    Optional<User> findByEmail ( @PathVariable String email) {
        return userService.findUserByEmail(email);
    }
    @PutMapping ("updateUser/{id}")
    User updateUser( @PathVariable String id, @RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateUser(id , userUpdateRequest);
    }
    @DeleteMapping("deleteUser/{id}")
    void deleteUser(@PathVariable String id) {
        userService.deleteUserById(id);
    }
    @GetMapping("/myi4")
    ApiResponse<UserResponse> getMyI4() {
        return ApiResponse.<UserResponse>builder()
                          .result(userService.getMyI4())
                          .build();
    }
    @GetMapping("/hi")
    String hi() {
        return "sontaypham";
    }
}
