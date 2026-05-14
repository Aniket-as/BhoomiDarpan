package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.GiftDeedRequest;
import com.bhoomidarpan.entity.enums.GiftDeedStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftDeedRepository extends JpaRepository<GiftDeedRequest, Long> {
    boolean existsByProperty_IdAndStatus(Long propertyId, GiftDeedStatus status);
    List<GiftDeedRequest> findByStatus(GiftDeedStatus status);
}