package com.bhoomidarpan.entity.enums;


import lombok.Data;

@Data
public class VisitDateDecisionRequest {
    private boolean approve;   // true = confirm, false = reject
    private String remarks;
}

