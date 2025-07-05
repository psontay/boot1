package com.boot1.Service;

import com.boot1.Entities.User;
import com.boot1.dto.request.UserCreationRequest;
import com.boot1.dto.response.UserResponse;
import com.boot1.exception.ApiException;
import com.boot1.mapper.UserMapper;
import com.boot1.repository.UserRepository;
import com.boot1.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@FieldDefaults( level = AccessLevel.PRIVATE)
public class UserServiceTest {
    @Autowired
    UserService userService;
    @MockitoBean
    UserMapper userMapper;
    @MockitoBean
    UserRepository userRepository;
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
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(userMapper.toUser(any())).thenReturn(user);
        Mockito.when(userRepository.save(any())).thenReturn(user);
        Mockito.when(userMapper.toUserResponse(any())).thenReturn(userResponse);
        // when
        var response = userService.createUser(userCreationRequest);
        // then
        Assertions.assertEquals("sontaypham" , response.getId());
        Assertions.assertEquals("Test", response.getUsername());

    }
    @Test
    void createUser_existingUsername_fail()  {
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(true);
        var exception = Assertions.assertThrows(ApiException.class , () -> userService.createUser(userCreationRequest));
        Assertions.assertEquals(-1, exception.getErrorCode().getCode());
    }
}
