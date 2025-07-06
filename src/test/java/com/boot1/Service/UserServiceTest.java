package com.boot1.Service;

import com.boot1.Entities.Role;
import com.boot1.Entities.User;
import com.boot1.dto.request.UserCreationRequest;
import com.boot1.dto.response.UserResponse;
import com.boot1.exception.ApiException;
import com.boot1.mapper.UserMapper;
import com.boot1.repository.RoleRepository;
import com.boot1.repository.UserRepository;
import com.boot1.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@FieldDefaults( level = AccessLevel.PRIVATE)
public class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    UserMapper userMapper;
    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    UserCreationRequest userCreationRequest;
    UserResponse userResponse;
    User user;
    LocalDate dob;
    @BeforeEach
    void initData() {
        dob = LocalDate.of(2005 , 1 , 1);
        userCreationRequest = UserCreationRequest.builder()
                                                 .username("Test")
                                                 .firstName("test")
                                                 .lastName("test")
                                                 .email("user@test@gmail.com")
                                                 .password("Testtest")
                                                 .dob(dob).build();
        userResponse = UserResponse.builder()
                .email("user@test@gmail.com")
                                   .username("Test")
                                   .firstName("test")
                                   .lastName("test")
                                   .id("sontaypham")
                                    .dob(dob).build();
        user = User.builder()
                .email("user@test@gmail.com")
                        .username("Test")
                        .firstName("test")
                        .lastName("test")
                        .dob(dob)
                .password("Testtest")
                        .id("sontaypham").build();
    }
    @Test
    void createUser_validRequest_success()  {
        // given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userMapper.toUser(any())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);
        // when
        var response = userService.createUser(userCreationRequest);
        // then
        assertEquals("sontaypham" , response.getId());
        assertEquals("Test", response.getUsername());

    }
    @Test
    void createUser_existingUsername_fail()  {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        var exception = Assertions.assertThrows(ApiException.class , () -> userService.createUser(userCreationRequest));
        assertEquals(-1, exception.getErrorCode().getCode());
    }
    @Test
    void findUserById_validId_success()  {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);
    }
    @Test
    void findUserById_notFoundId_fail()  {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        var exception = Assertions.assertThrows(RuntimeException.class , () -> userService.findUserById(anyString()));
        assertEquals("User not found", exception.getMessage());
    }
    @Test
    void getUsers_hasUsers_success() {

        User user = new User();
        user.setRoles(Set.of(Role.builder().name("ADMIN").build()));

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(new UserResponse());

        // Act
        List<UserResponse> result = userService.getUsers();

        // Assert
        assertEquals(1, result.size());
    }
}
