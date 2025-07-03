package com.boot1.service;

import com.boot1.repository.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InvalidatedTokenCleanupJob {
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    @Scheduled( fixedRate = 3600000)
    public void cleanInvalidatedTokens() {
        Date now = new Date();
        invalidatedTokenRepository.deleteByExpTimeBefore(now);
        System.out.println("Clean up completed! Time : " +  now);
    }
}
