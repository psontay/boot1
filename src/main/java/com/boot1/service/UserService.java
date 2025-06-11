package com.boot1.service;

import com.boot1.Entities.User;
import com.boot1.dto.request.UserCreationRequest;
import com.boot1.dto.request.UserUpdateRequest;
import com.boot1.dto.response.UserResponse;
import com.boot1.enums.Role;
import com.boot1.exception.ApiException;
import com.boot1.exception.ErrorCode;
import com.boot1.mapper.UserMapper;
import com.boot1.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    public User createUser(UserCreationRequest request) {
        if ( userRepository.existsByUsername(request.getUsername()))
            throw new ApiException(ErrorCode.USER_EXISTS);
        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);
        return userRepository.save(user);
    }
    public UserResponse findUserById(@NonNull String id) {
        return userMapper.toUserResponse(userRepository.
                                                 findById(id).
                                                 orElseThrow(() -> new RuntimeException("User " + "not " + "found")));
    }
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> findUsersByFirstName ( String firstName  ) {
        return userRepository.findByFirstNameContaining(firstName);
    }
    public List<User> findUserByFirstNameAndLastName(String firstName, String lastName) {
        return userRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public List<User> findUserByLastName(String lastName) {
        return userRepository.findByLastNameContaining(lastName);
    }
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElse(null);
        userMapper.updateUser(user , request);
        return userRepository.save(user);
    }
    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }

}
