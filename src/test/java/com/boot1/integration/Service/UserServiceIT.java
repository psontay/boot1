package com.boot1.integration.Service;

import com.boot1.Entities.Role;
import com.boot1.Entities.User;
import com.boot1.dto.request.UserCreationRequest;
import com.boot1.dto.response.UserResponse;
import com.boot1.enums.RoleName;
import com.boot1.exception.ApiException;
import com.boot1.repository.RoleRepository;
import com.boot1.repository.UserRepository;
import com.boot1.mapper.UserMapper;
import com.boot1.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceIT {

    @Autowired UserService userService;
    @Autowired
    MockMvc mockMvc;

    @MockBean UserRepository userRepository;

    @MockBean UserMapper userMapper;

    @MockBean PasswordEncoder passwordEncoder;

    @MockBean RoleRepository roleRepository;

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
                   .roles(Set.of(Role.builder().name(RoleName.USER.name()).build()))
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
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(Role.builder().name(RoleName.USER.name()).build()));
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
        when(userMapper.toUser(userCreationRequest)).thenReturn(user);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());
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
        assertEquals("sontaypham", result.getFirst().getId());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUsers_asUser_accessDenied() throws Exception {
        mockMvc.perform(get("/users/list"))
               .andExpect(status().isForbidden());
    }

    @Test
    void getUsers_noAuthentication_unauthenticated() throws Exception {
        mockMvc.perform(get("/users/list"))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    @WithMockUser( username = "Test")
    void findUserById_validRequest_success() throws Exception {
        // given
        when(userRepository.findById("sontaypham")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        // when then
        mockMvc.perform(get("/users/findById/sontaypham")).andExpect(status().isOk());
    }
    @Test
    @WithMockUser( username = "test")
    void findUserById_usernameNotEquals_fail() throws Exception {
        // given
        when(userRepository.findById("sontaypham")).thenReturn(Optional.empty());
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        // when then
        mockMvc.perform(get("/users/findById/sontaypham")).andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser( roles = "ADMIN")
    void findUserByUsername_validRequest_success() throws Exception {
        // given
        when(userRepository.findByUsername("Test")).thenReturn(Optional.of(user));
        mockMvc.perform(get("/users/findByUsername/Test")).andExpect(status().isOk());
    }
    @Test
    @WithMockUser( roles = "ADMIN")
    void findUserByUsername_usernameNotFound_fail() throws Exception {
        // given
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        // when then
        mockMvc.perform(get("/users/findByUsername/test")).andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser( username = "Test" , roles = "ADMIN")
    void findUserByEmail_validRequest_success() throws Exception {
        // given
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        // when then
        mockMvc.perform(get("/users/findByEmail/user@test.com")).andExpect(status().isOk());
    }
    @Test
    @WithMockUser ( username = "Test" , roles = "ADMIN")
    void findUserByEmail_emailNotFound_fail() throws Exception {
        // given
        when(userRepository.findByEmail("test")).thenReturn(Optional.empty());
        //when then
        mockMvc.perform(get("/users/findByEmail/test")).andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser ( username = "Test" , roles = "ADMIN")
    void findByFirstName_validRequest_success() throws Exception {
        // given
        when(userRepository.findByFirstNameContaining("test")).thenReturn(List.of(user));
        // when then
        mockMvc.perform(get("/users/findByFirstName/test")).andExpect(status().isOk());
    }
    @Test
    @WithMockUser( username = "Test" , roles = "ADMIN")
    void findByFirstName_usernameNotFound_fail() throws Exception {
        // given
        when(userRepository.findByFirstNameContaining("z")).thenReturn(List.of());
        // when then
        mockMvc.perform(get("/users/findByFirstName/z")).andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser( username = "Test" , roles = "ADMIN")
    void findByFirstAndLastName_validRequest_success() throws Exception {
        // given
        when(userRepository.findByFirstNameAndLastName("test" , "test")).thenReturn(List.of(user));
        // when then
        mockMvc.perform(get("/users/findByFirstAndLastName/test/test")).andExpect(status().isOk());
    }
    @Test
    @WithMockUser( username = "Test" , roles = "ADMIN")
    void findByFirstAndLastName_notFound_fail() throws Exception {
        // given
        when(userRepository.findByFirstNameAndLastName("z" , "z")).thenReturn(List.of());
        // when then
        mockMvc.perform(get("/users/findByFirstAndLastName/z/z")).andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser ( username = "Test" , roles = "ADMIN")
    void findByLastName_validRequest_success() throws Exception {
        // given
        when(userRepository.findByLastNameContaining("test")).thenReturn(List.of(user));
        // when then
        mockMvc.perform(get("/users/findByLastName/test")).andExpect(status().isOk());
    }
    @Test
    @WithMockUser( username = "Test" , roles = "ADMIN")
    void findByLastName_usernameNotFound_fail() throws Exception {
        // given
        when(userRepository.findByLastNameContaining("z")).thenReturn(List.of());
        // when then
        mockMvc.perform(get("/users/findByLastName/z")).andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser( username = "Test" , roles = "ADMIN")
    void deleteUser_asAdminRole_success() throws Exception {
        // given
        String userIdToDelete = "anyId";
        // when
        userService.deleteUser(userIdToDelete);
        // then
        verify(userRepository, times(1)).deleteById(userIdToDelete);
    }
}
