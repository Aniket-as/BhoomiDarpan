package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class ConsentRequest {
    private boolean approve;
    private String remarks;
}
