package com.example.boot1.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UserCreationRequest {
    @Size(min = 3 , message = "Username must be at least 3 characters")
    private String username;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dcb;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDcb() {
        return dcb;
    }

    public void setDcb(LocalDate dcb) {
        this.dcb = dcb;
    }
}
