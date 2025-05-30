package com.example.boot1.dto.request;

import java.time.LocalDate;

public class UserUpdateRequest {
    private String firstName , lastName , password , email;
    private LocalDate dcb;

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDcb() {
        return dcb;
    }

    public void setDcb(LocalDate dcb) {
        this.dcb = dcb;
    }
}
