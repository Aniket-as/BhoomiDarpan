package com.bhoomidarpan.dto;


import lombok.Data;
@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String aadhaarNumber;
    private String pan;
    private String mobile;
    private String role;
}