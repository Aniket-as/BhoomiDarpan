package com.bhoomidarpan.dto;

import lombok.Data;

@Data
public class DashboardStatsResponse {
    private long properties;
    private long transactions;
    private long mutationPending;
    private long certificates;
}
