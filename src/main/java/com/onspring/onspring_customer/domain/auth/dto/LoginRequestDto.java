package com.onspring.onspring_customer.domain.auth.dto;

import lombok.ToString;

@ToString
public class LoginRequestDto {
    private String userName;
    private String phone;
    private String password;

    // Getters and setters
    public String getUserName() { return userName; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
}
