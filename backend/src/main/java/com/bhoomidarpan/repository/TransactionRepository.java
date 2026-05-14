package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.PropertyTransaction;
import com.bhoomidarpan.entity.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<PropertyTransaction, Long> {

    long countByBuyerIdAndTransactionStatus(
            Long buyerId,
            TransactionStatus transactionStatus
    );

    boolean existsByPropertyIdAndTransactionStatus(
            Long propertyId,
            TransactionStatus status
    );

    long countByPropertyId(Long propertyId);

}
