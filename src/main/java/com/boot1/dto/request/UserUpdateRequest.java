package com.boot1.dto.request;

import com.boot1.validator.DobConstraint;
import com.boot1.validator.MailConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String firstName , lastName , password;
    @MailConstraint(domain = "user@" , message = "INVALID_EMAIL_TYPE")
    String email;
    @DobConstraint( min = 18 , message = "INVALID_DATE_OF_BIRTH")
    LocalDate dob;
    Set<String> roles;
}
