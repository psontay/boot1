package com.boot1.service;

import com.boot1.Entities.Role;
import com.boot1.Entities.User;
import com.boot1.dto.request.UserCreationRequest;
import com.boot1.dto.request.UserUpdateRequest;
import com.boot1.dto.response.UserResponse;
import com.boot1.enums.RoleName;
import com.boot1.exception.ApiException;
import com.boot1.exception.ErrorCode;
import com.boot1.mapper.UserMapper;
import com.boot1.repository.RoleRepository;
import com.boot1.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableMethodSecurity
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    public User createUser(UserCreationRequest request) {
        if ( userRepository.existsByUsername(request.getUsername()))
            throw new ApiException(ErrorCode.USER_EXISTS);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName(RoleName.USER)
                            .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userRepository.save(user);
    }
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse findUserById(@NonNull String id) {
        log.info("Find user by id: {}", id);
        return userMapper.toUserResponse(userRepository.
                                                 findById(id).
                                                 orElseThrow(() -> new RuntimeException("User " + "not " + "found")));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("method getUser");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse).toList();
    }
    public UserResponse getMyI4 ( ) {
        SecurityContext contextHolder = SecurityContextHolder.getContext();
        Authentication authentication = contextHolder.getAuthentication();
        String userName = authentication.getName();
        User existsUser =
                userRepository.findByUsername(userName).orElseThrow( () -> new ApiException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(existsUser);
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
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }

}
