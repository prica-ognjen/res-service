package com.badpc.res.userservice;

public class UserMailDto {

    private String email;

    public UserMailDto(String email) {
        this.email = email;
    }

    public UserMailDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
