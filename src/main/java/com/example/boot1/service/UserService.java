package com.example.boot1.service;

import com.example.boot1.Entities.User;
import com.example.boot1.dto.request.UserCreationRequest;
import com.example.boot1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public User createUser(UserCreationRequest request) {
        User user = new User();
        user.setDcb(request.getDcb());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return userRepository.save(user);
    }
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    public List<User> findUsersByFirstName ( String firstName  ) {
        return userRepository.findByFirstNameContaining(firstName);
    }
    public List<User> findUserByFirstNameAndLastName(String firstName, String lastName) {
        return userRepository.findByFirstNameAndLastName(firstName, lastName);
    }
    public Optional<User> findUserById(@NonNull String id) {
        return userRepository.findById(id);
    }
    public List<User> findUserByLastName(String lastName) {
        return userRepository.findByLastNameContaining(lastName);
    }
    public User updateUser(String id, String firstName, String lastName, LocalDate dcb, String password) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDcb(dcb);
            user.setPassword(password);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User with ID " + id + " not found");
        }
    }

}
