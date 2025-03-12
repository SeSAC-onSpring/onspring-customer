package com.onspring.onspring_customer.domain.common.entity;

import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customer_franchise")
public class CustomerFranchise extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

}