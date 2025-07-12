package com.boot1.service;

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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
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

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(ErrorCode.USERNAME_EXISTS);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var role = roleRepository.findByName(RoleName.USER.name())
                                 .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
        Set<com.boot1.Entities.Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse findUserById(String id) {
        var user = userRepository.findById(id)
                                 .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(userMapper::toUserResponse)
                             .toList();
    }

    public UserResponse getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userRepository.findByUsername(auth.getName())
                                 .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse findUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
                                 .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse findUserByEmail(String email) {
        var user = userRepository.findByEmail(email)
                                 .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> findByFirstName(String firstName) {
        return userRepository.findByFirstNameContaining(firstName)
                             .stream()
                             .map(userMapper::toUserResponse)
                             .toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> findByFirstAndLastName(String firstName, String lastName) {
        return userRepository.findByFirstNameAndLastName(firstName, lastName)
                             .stream()
                             .map(userMapper::toUserResponse)
                             .toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> findByLastName(String lastName) {
        return userRepository.findByLastNameContaining(lastName)
                             .stream()
                             .map(userMapper::toUserResponse)
                             .toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        var user = userRepository.findById(id)
                                 .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXISTS));
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userMapper.updateUser(user, request);
        user.setRoles(roleRepository.findByNameIn(request.getRoles()));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}