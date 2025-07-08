package com.boot1.intergation.Service;

import com.boot1.Boot1Application;
import com.boot1.Entities.Role;
import com.boot1.Entities.User;
import com.boot1.dto.request.UserCreationRequest;
import com.boot1.dto.response.UserResponse;
import com.boot1.exception.ApiException;
import com.boot1.repository.RoleRepository;
import com.boot1.repository.UserRepository;
import com.boot1.mapper.UserMapper;
import com.boot1.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceIT {

    @Autowired UserService userService;
    @Autowired
    MockMvc mockMvc;

    UserRepository userRepository;

     UserMapper userMapper;

     PasswordEncoder passwordEncoder;

     RoleRepository roleRepository;

    User user;
    UserResponse userResponse;
    UserCreationRequest userCreationRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                   .id("sontaypham")
                   .username("Test")
                   .firstName("test")
                   .lastName("test")
                   .email("user@test.com")
                   .password("irrelevant")
                   .dob(LocalDate.of(2000, 1, 1))
                   .roles(Set.of(Role.builder().name("ADMIN").build()))
                   .build();
        userCreationRequest = UserCreationRequest.builder()
                                                 .username("Test")
                                                 .firstName("test")
                                                 .lastName("test")
                                                 .email("user@test.com")
                                                 .password("irrelevant")
                                                 .dob(LocalDate.of(2000, 1, 1))
                                                 .build();
        userResponse = UserResponse.builder()
                                   .id("sontaypham")
                                   .username("Test")
                                   .firstName("test")
                                   .lastName("test")
                                   .email("user@test.com")
                                   .dob(LocalDate.of(2000, 1, 1))
                                   .build();
    }
    @Test
    @WithMockUser(username = "Test")
    void createUser_validRequest_success() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userMapper.toUser(userCreationRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        UserResponse userResponse = userService.createUser(userCreationRequest);
        assertEquals("Test", userResponse.getUsername());
        assertEquals("sontaypham" , userResponse.getId());
    }

    @Test
    @WithMockUser(username = "Test")
    void createUser_usernameExists_fail() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        var exception = assertThrows(ApiException.class , () -> userService.createUser(userCreationRequest));
        assertEquals("Username already exists", exception.getMessage());
    }
    @Test
    @WithMockUser(username = "Test")
    void createUser_roleInvalid_fail() {
        // given
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());
        var exception = assertThrows(ApiException.class , () -> userService.createUser(userCreationRequest));
        assertEquals("Role not found", exception.getMessage());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsers_asAdmin_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        List<UserResponse> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("sontaypham", result.get(0).getId());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUsers_asUser_accessDenied() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        assertThrows(
                AccessDeniedException.class,
                () -> userService.getUsers(),
                "User with ROLE_USER must not be allowed to call getUsers()"
                    );
    }

    @Test
    void getUsers_noAuthentication_unauthenticated() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> userService.getUsers(),
                "Unauthenticated invocation must throw AuthenticationCredentialsNotFoundException"
                    );
    }


}
