package com.boot1.dto.request;

import com.boot1.validator.DobConstraint;
import com.boot1.validator.MailConstraint;
import com.boot1.validator.PasswordConstraint;
import com.boot1.validator.UsernameConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3 , message = "USERNAME_LENGTH_INVALID")
    @UsernameConstraint( message = "USERNAME_TYPE_INVALID")
    String username;
    @Size(min = 6, message = "PASSWORD_INVALID")
    @PasswordConstraint( message = "PASSWORD_TYPE_INVALID")
    String password;
    @NotBlank(message = "Firstname is required")
    String firstName;
    @NotBlank(message = "Lastname is required")
    String lastName;
    @DobConstraint(min = 18 , message = "INVALID_DATE_OF_BIRTH")
    LocalDate dob;
    @NotBlank(message = "Email is required")
    @MailConstraint(domain = "user@" , message = "INVALID_EMAIL_TYPE")
    String email;
}
