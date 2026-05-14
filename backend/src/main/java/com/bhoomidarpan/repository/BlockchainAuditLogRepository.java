package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.BlockchainAuditLog;
import com.bhoomidarpan.entity.enums.BlockchainActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockchainAuditLogRepository extends JpaRepository<BlockchainAuditLog, Long> {
    List<BlockchainAuditLog> findByPropertyId(Long propertyId);
    List<BlockchainAuditLog> findByActionType(BlockchainActionType actionType);
    boolean existsByTransactionHash(String transactionHash);
}