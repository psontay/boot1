package com.example.boot1.repository;

import com.example.boot1.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByFirstNameContaining(String firstName);
    List<User> findByFirstNameAndLastName(String userFirstName, String userLastName);
    Optional<User> findById(String id);
}
