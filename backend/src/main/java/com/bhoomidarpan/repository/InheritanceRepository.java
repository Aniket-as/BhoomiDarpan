package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.InheritanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InheritanceRepository
        extends JpaRepository<InheritanceRequest, Long> {

    List<InheritanceRequest> findByRequestedById(Long userId);

    List<InheritanceRequest> findByStatus(String status);

}
