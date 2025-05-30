package com.example.boot1.controller;

import com.example.boot1.Entities.User;
import com.example.boot1.dto.request.UserCreationRequest;
import com.example.boot1.dto.request.UserUpdateRequest;
import com.example.boot1.dto.response.ApiResponse;
import com.example.boot1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/create")
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setCode(1);
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }
    @GetMapping("/list")
    List<User> getUsers() {
        return userService.getUsers();
    }
    @GetMapping("/findByFirstName/{firstName}")
    List<User> findByFirstName(@PathVariable String firstName) {
        return userService.findUsersByFirstName(firstName);
    }
    @GetMapping("/findByFirstNameAndLastName")
    List<User> findByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        return userService.findUserByFirstNameAndLastName(firstName, lastName);
    }
    @GetMapping("findById/{id}")
    Optional<User> findById(@PathVariable String id) {
        return userService.findUserById(id);
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
        return userService.updateUser(id ,userUpdateRequest.getFirstName() , userUpdateRequest.getLastName() ,
                                      userUpdateRequest.getDcb() , userUpdateRequest.getPassword() ,
                                      userUpdateRequest.getEmail());
    }
    @DeleteMapping("deleteUser/{id}")
    void deleteUser(@PathVariable String id) {
        userService.deleteUserById(id);
    }
}
