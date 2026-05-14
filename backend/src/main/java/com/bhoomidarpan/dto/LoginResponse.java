package com.bhoomidarpan.dto;


import lombok.Data;
@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private String role;
}