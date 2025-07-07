package com.boot1.Service;

import com.boot1.Boot1Application;
import com.boot1.Entities.Role;
import com.boot1.Entities.User;
import com.boot1.dto.response.UserResponse;
import com.boot1.repository.UserRepository;
import com.boot1.mapper.UserMapper;
import com.boot1.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = Boot1Application.class
)
@ActiveProfiles("test")
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceIntegrationTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserMapper userMapper;

    @MockBean
    PasswordEncoder passwordEncoder;

    User user;
    UserResponse userResponse;

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
    @Test
    @WithMockUser( username = "Test")
    void getMyI4_success() {
        User fakeUser = User.builder()
                            .id("sontaypham")
                            .username("Test")
                            .firstName("test")
                            .lastName("test")
                            .email("user@test@gmail.com")
                            .password("irrelevant")
                            .dob(LocalDate.of(2000, 1, 1))
                            .roles(Set.of(Role.builder().name("USER").build()))
                            .build();
        UserResponse fakeUserResponse = UserResponse.builder()
                .id("sontaypham")
                .username("Test")
                .firstName("test")
                .lastName("test")
                .email("user@test@gmail.com")
                .dob(LocalDate.of(2000, 1, 1))
                .roles(Set.of(Role.builder().name("USER").build()))
                                                    .build();
        when(userRepository.findByUsername("Test")).thenReturn(Optional.of(fakeUser));
        when(userMapper.toUserResponse(fakeUser)).thenReturn(fakeUserResponse);
        UserResponse result = userService.getMyI4();
        assertEquals(fakeUserResponse, result);
    }
}
