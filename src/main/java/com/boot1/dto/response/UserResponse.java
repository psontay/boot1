package com.boot1.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.boot1.Entities.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    String email;
    Set<Role> roles;
}
