package com.boot1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Boot1Application {
    public static void main(String[] args) {
        SpringApplication.run(Boot1Application.class, args);
    }

}
