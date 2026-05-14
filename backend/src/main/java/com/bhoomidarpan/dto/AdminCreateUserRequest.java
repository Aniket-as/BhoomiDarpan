package com.bhoomidarpan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateUserRequest {

    private String name;
    private String email;
    private String password;
    private String mobile;
    private String aadhaarNumber;
    private String pan;
    private String role; // USER, TEHSILDAR, SUB_REGISTRAR
}

