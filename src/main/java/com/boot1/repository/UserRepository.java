package com.boot1.repository;

import com.boot1.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    List<User> findByFirstNameContaining(String firstName);
    List<User> findByLastNameContaining(String lastName);
    List<User> findByFirstNameAndLastName(String userFirstName, String userLastName);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
