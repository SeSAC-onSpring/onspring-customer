package com.onspring.onspring_customer.domain.common.repository;

import com.onspring.onspring_customer.domain.common.entity.PlatformAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformAdminRepository extends JpaRepository<PlatformAdmin, Long> {
    PlatformAdmin findByUserName(String username);
}