package com.onspring.onspring_customer.domain.customer.repository;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

import java.util.List;

public interface PartyRepository extends JpaRepository<Party, Long> {
    List<Party> findByNameContainsAllIgnoreCase(@Nullable String name);
}