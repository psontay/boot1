package com.boot1.unit.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.boot1.dto.request.UserCreationRequest;
import com.boot1.dto.request.UserUpdateRequest;
import com.boot1.dto.response.UserResponse;
import com.boot1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestPropertySource("/test.properties")
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    UserCreationRequest userCreationRequest;
    UserResponse userResponse;
    LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(2005, 1, 1);
        userCreationRequest = UserCreationRequest.builder()
                .username("Test")
                .firstName("test")
                .lastName("test")
                .email("user@test@gmail.com")
                .password("Testtest")
                .dob(dob)
                .build();
        userResponse = UserResponse.builder()
                .username("Test")
                .dob(dob)
                .firstName("test")
                .lastName("test")
                .id("sontaypham")
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonBody = objectMapper.writeValueAsString(userCreationRequest);
        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);
        // when then
        mockMvc.perform(post("/users/create").content(jsonBody).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1))
                .andExpect(jsonPath("result.id").value("sontaypham"));
    }

    @Test
    void createUser_usernameMissingUpperCase_fail() throws Exception {
        // given
        userCreationRequest.setUsername("test");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonBody = objectMapper.writeValueAsString(userCreationRequest);
        // when then
        mockMvc.perform(post("/users/create").content(jsonBody).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(-15))
                .andExpect(jsonPath("msg").value("Username must be at least 1 character upper"));
    }

    @Test
    void createUser_usernameTooShort_fail() throws Exception {
        // given
        userCreationRequest.setUsername("Te");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonBody = objectMapper.writeValueAsString(userCreationRequest);
        // when then
        mockMvc.perform(post("/users/create").content(jsonBody).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(-2))
                .andExpect(jsonPath("msg").value("Username must be at least 3 characters"));
    }

    @Test
    void createUser_passwordTooShort_fail() throws Exception {
        // given
        userCreationRequest.setPassword("Te");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonBody = objectMapper.writeValueAsString(userCreationRequest);
        // when then
        mockMvc.perform(post("/users/create").content(jsonBody).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(-3))
                .andExpect(jsonPath("msg").value("Password must be at least 6 characters"));
    }

    @Test
    void createUser_passwordMissingUpperCase_fail() throws Exception {
        // given
        userCreationRequest.setPassword("testpassword");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonBody = objectMapper.writeValueAsString(userCreationRequest);
        // when then
        mockMvc.perform(post("/users/create").content(jsonBody).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(-16))
                .andExpect(jsonPath("msg").value("Password must be at least 1 character upper"));
    }

    @Test
    void createUser_firstNameBlank_fail() throws Exception {
        // given
        userCreationRequest.setFirstName("");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonBody = objectMapper.writeValueAsString(userCreationRequest);
        // when then
        mockMvc.perform(post("/users/create").content(jsonBody).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(-17))
                .andExpect(jsonPath("msg").value("First name is empty"));
    }

    @Test
    void createUser_lastNameBlank_fail() throws Exception {
        // given
        userCreationRequest.setLastName("");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonBody = objectMapper.writeValueAsString(userCreationRequest);
        // when then
        mockMvc.perform(post("/users/create").content(jsonBody).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(-18))
                .andExpect(jsonPath("msg").value("Last name is empty"));
    }

    @Test
    void createUser_dobUnderage_fail() throws Exception {
        // given
        LocalDate underageDob = LocalDate.now().minusYears(17);
        userCreationRequest.setDob(underageDob);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String jsonBody = objectMapper.writeValueAsString(userCreationRequest);
        // when then
        mockMvc.perform(post("/users/create").content(jsonBody).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(-11))
                .andExpect(jsonPath("msg").value("Invalid date of birth , must be at least 18 years old"));
    }

    @Test
    void createUser_emailBlank_fail() throws Exception {
        // given
        userCreationRequest.setEmail("");
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String jsonBody = objectMapper.writeValueAsString(userCreationRequest);
        // when then
        mockMvc.perform(post("/users/create").content(jsonBody).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(-19))
                .andExpect(jsonPath("msg").value("Email is empty"));
    }

    @Test
    @WithMockUser(username = "TestUser")
    void getMyProfile_validRequest_success() throws Exception {
        userResponse.setUsername("TestUser");

        Mockito.when(userService.getMyProfile()).thenReturn(userResponse);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.result.username").value("TestUser"));
    }

    @Test
    @WithMockUser(username = "AdminUser", roles = "ADMIN")
    void getUsers_hasAdminRole_success() throws Exception {
        Mockito.when(userService.getUsers()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/users/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.result[0].id").value("sontaypham"));
    }

    @Test
    @WithMockUser(username = "TestUser")
    void findUserById_validOwner_success() throws Exception {
        userResponse.setUsername("TestUser");
        Mockito.when(userService.findUserById("sontaypham")).thenReturn(userResponse);

        mockMvc.perform(get("/users/findById/sontaypham"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.result.username").value("TestUser"));
    }

    @Test
    @WithMockUser(username = "AdminUser", roles = "ADMIN")
    void updateUser_validRequest_success() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("UpdatedFirst");
        updateRequest.setLastName("UpdatedLast");
        updateRequest.setPassword("NewPassword");
        updateRequest.setRoles(Set.of("ADMIN"));

        Mockito.when(userService.updateUser(Mockito.eq("sontaypham"), Mockito.any()))
                .thenReturn(userResponse);

        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/users/update/sontaypham")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.result.id").value("sontaypham"));
    }

    @Test
    @WithMockUser(username = "AdminUser", roles = "ADMIN")
    void deleteUser_hasAdminRole_success() throws Exception {
        mockMvc.perform(delete("/users/delete/sontaypham"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("Deleted successfully"));

        Mockito.verify(userService).deleteUser("sontaypham");
    }
}
