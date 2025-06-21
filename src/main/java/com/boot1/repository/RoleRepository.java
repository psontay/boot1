package com.boot1.repository;

import com.boot1.Entities.Role;
import com.boot1.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName( String roleName);
    boolean existsByName( String roleName);
    Optional<Role> findByDescription(String description);
    void deleteByName( String roleName);
    List<Role> findAllByNameContainingIgnoreCase(String keyword);
}
