package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class DisputeActionRequest {
    private boolean approve;
    private String remarks;
}
