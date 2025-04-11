package com.onspring.onspring_customer.domain.user.repository;

import com.onspring.onspring_customer.domain.user.entity.EndUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EndUserRepository extends JpaRepository<EndUser, Long> {
    EndUser findByPhone(String phone);
}