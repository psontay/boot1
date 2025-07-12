package com.boot1.dto.request;

import java.time.LocalDate;
import java.util.Set;

import com.boot1.validator.DobConstraint;
import com.boot1.validator.MailConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String firstName, lastName, password;

    @MailConstraint(domain = "user@", message = "INVALID_EMAIL_TYPE")
    String email;

    @DobConstraint(min = 18, message = "INVALID_DATE_OF_BIRTH")
    LocalDate dob;

    Set<String> roles;
}
