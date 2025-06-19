package com.boot1.repository;

import com.boot1.Entities.Permission;
import com.boot1.dto.request.PermissionRequest;
import com.boot1.dto.response.PermissionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission , String> {
    boolean existsByName(String permissionName);
    Optional<Permission> findByName(String permissionName);
    Optional<Permission> findByDescription(String permissionDescription);
    void delete(String permissionName);

    List<Permission> findAllByNameContainingIgnoreCase(String keyword );
}
