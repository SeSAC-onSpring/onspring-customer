package com.onspring.onspring_customer.domain.franchise.repository;

import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    @Query("SELECT DISTINCT f FROM Franchise f " +
            "JOIN CustomerFranchise cf ON cf.franchise = f " +
            "JOIN Party p ON p.customer.id = cf.customer.id " +
            "JOIN Point p1 ON p1.party.customer.id = p.customer.id " +
            "WHERE p1.endUser.id = :endUserId")
    Page<Franchise> findAllFranchiseByEndUserId(@Param("endUserId") Long endUserId, Pageable pageable);

    Franchise findByUserName(String userName);
}