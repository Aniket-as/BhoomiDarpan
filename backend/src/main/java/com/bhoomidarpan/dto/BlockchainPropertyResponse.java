package com.bhoomidarpan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockchainPropertyResponse {

    private String propertyCode;
    private String ownerWallet;
    private String documentHash;
    private long registeredAt;
    private int status; // 1 = ACTIVE, 2 = DISPUTED
}
