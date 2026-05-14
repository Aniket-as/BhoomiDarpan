package com.bhoomidarpan.entity.enums;

public enum RegistrationStatus {

    APPOINTMENT_SCHEDULED,
    VERIFICATION_PENDING,
    VERIFIED,

    ON_HOLD,              // 🔥 Added for AI suspicious cases
    BLOCKCHAIN_FAILED,
    REVERSED,
    APPROVED,
    REJECTED
}
