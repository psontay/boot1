package com.boot1.repository;

import com.boot1.Entities.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken , String> {
    void deleteByExpTimeBefore(Date now);
}
