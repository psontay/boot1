package com.boot1.dto.request;

import com.boot1.Entities.Role;
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
    String firstName , lastName , password , email;
    LocalDate dob;
    Set<String> roles;
}
