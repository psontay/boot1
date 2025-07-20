package com.boot1.configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.boot1.Entities.Permission;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.boot1.Entities.Role;
import com.boot1.Entities.User;
import com.boot1.enums.RoleName;
import com.boot1.exception.ApiException;
import com.boot1.exception.ErrorCode;
import com.boot1.repository.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;
    @Value("${spring.app.default-admin.username}")
    String username;
    @Value("${spring.app.default-admin.password}")
    String password;
    @Bean
    @ConditionalOnProperty(
            prefix = "spring.datasource",
            name = "driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver"
    )

    ApplicationRunner applicationRunner(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        PermissionRepository permissionRepository)
    {
        return args -> {
            log.warn("Running ApplicationRunner to seed admin...");
            try {
                List<String> defaultPermissions = List.of("USER_READ", "USER_CREATE", "USER_UPDATE", "USER_DELETE",
                                                          "ROLE_MANAGE");
                for(String perm : defaultPermissions) {
                    if(permissionRepository.findByName(perm).isEmpty()) {
                        permissionRepository.save(new Permission(perm, "Permission for " + perm));
                    }
                }

                if(roleRepository.findByName(RoleName.ADMIN.name()).isEmpty()) {
                    Set<Permission> permissions = new HashSet<>(permissionRepository.findAll());
                    roleRepository.save(new Role(RoleName.ADMIN.name(), "ADMIN with all permissions", permissions));
                }

                if(userRepository.findByUsername(username).isEmpty()) {
                    Role adminRole = roleRepository
                            .findByName(RoleName.ADMIN.name())
                            .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));

                    User user = User.builder()
                                    .username(username)
                                    .password(passwordEncoder.encode(password))
                                    .roles(Set.of(adminRole))
                                    .build();
                    userRepository.save(user);
                    log.warn("Admin user has been created with default password");
                }
            }catch ( Exception e ){
                log.error(e.getMessage());
        }
    };
    }
    @PostConstruct
    public void printDriverClassName() {
        log.warn(">>> Driver: " + System.getProperty("spring.datasource.driverClassName"));
    }


}
