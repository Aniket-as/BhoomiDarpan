package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    long countByUserId(Long userId);

    List<Certificate> findByUserId(Long userId);

}
