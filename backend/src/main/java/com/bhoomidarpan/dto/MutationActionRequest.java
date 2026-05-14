package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class MutationActionRequest {
    private boolean approve;
    private String remarks;
}
