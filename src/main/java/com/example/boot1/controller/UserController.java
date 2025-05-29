package com.example.boot1.controller;

import com.example.boot1.Entities.User;
import com.example.boot1.dto.request.UserCreationRequest;
import com.example.boot1.dto.request.UserUpdateRequest;
import com.example.boot1.service.UserService;
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
    User createUser(@RequestBody UserCreationRequest request) {
        return userService.createUser(request);
    }
    @GetMapping("/list")
    List<User> getUsers() {
        return userService.getUsers();
    }
    @GetMapping("/findByFirstName")
    List<User> findByFirstName(@RequestParam String firstName) {
        return userService.findUsersByFirstName(firstName);
    }
    @GetMapping("/findByFirstNameAndLastName")
    List<User> findByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {
        return userService.findUserByFirstNameAndLastName(firstName, lastName);
    }
    @GetMapping("/findById")
    Optional<User> findById(@RequestParam String id) {
        return userService.findUserById(id);
    }
    @GetMapping("/findByLastName")
    List<User> findByLastName(@RequestParam String lastName) {
        return userService.findUserByLastName(lastName);
    }
    @PutMapping ("/{id}")
    User updateUser( @PathVariable String id, @RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateUser(id ,userUpdateRequest.getFirstName() , userUpdateRequest.getLastName() ,
                                      userUpdateRequest.getDcb() , userUpdateRequest.getPassword());
    }
    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }
}
