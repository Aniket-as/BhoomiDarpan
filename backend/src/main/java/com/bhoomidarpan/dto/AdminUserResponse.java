package com.bhoomidarpan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserResponse {
    private Long id;
    private String name;
    private String email;
    private String mobile;
    private String role;
    private boolean active;
}
