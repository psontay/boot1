package com.boot1.dto.request;

import com.boot1.Entities.Role;
import com.boot1.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

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
    @DobConstraint(min = 18 , message = "INVALID_DATE_OF_BIRTH")
    @PastOrPresent(message = "Dob must be at present or past")
    LocalDate dob;
    @NotBlank(message = "Email is required")
    @Email(message = "Must an email form")
    String email;
}
