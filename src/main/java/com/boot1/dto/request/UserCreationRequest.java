package com.boot1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3 , message = "USERNAME_INVALID")
    String username;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "PASSWORD_INVALID")
    String password;
    @NotBlank(message = "Firstname is required")
    String firstName;
    @NotBlank(message = "Lastname is required")
    String lastName;
    @PastOrPresent(message = "Dob must be at present or past")
    LocalDate dob;
    @NotBlank(message = "Email is required")
    @Email(message = "Must an email form")
    String email;


}
