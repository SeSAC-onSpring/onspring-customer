package com.onspring.onspring_customer.domain.customer.repository;

import com.onspring.onspring_customer.domain.customer.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUserName(String username);
}