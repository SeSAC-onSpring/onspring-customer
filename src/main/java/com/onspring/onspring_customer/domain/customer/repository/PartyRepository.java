package com.onspring.onspring_customer.domain.customer.repository;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;


public interface PartyRepository extends JpaRepository<Party, Long> {

    @Query("SELECT p.party FROM Point p WHERE p.endUser.id = :endUserId ORDER BY p.createdAt ASC")
    List<Party> findOldestPartyByEndUserId(@Param("endUserId") Long endUserId);
    List<Party> findByNameContainsAllIgnoreCase(@Nullable String name);
}