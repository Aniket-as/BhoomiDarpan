package com.bhoomidarpan.repository;

import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPan(String pan);
    boolean existsByEmail(String email);
    boolean existsByAadhaarNumber(String aadhaarNumber);
    boolean existsByPan(String pan);
    java.util.List<User> findByRole(Role role);
    List<User> findByRoleIn(List<Role> roles);
    Optional<User> findByAadhaarNumber(String aadhaarNumber);

}