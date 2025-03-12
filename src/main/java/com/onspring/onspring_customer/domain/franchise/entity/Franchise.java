package com.onspring.onspring_customer.domain.franchise.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.common.entity.CustomerFranchise;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "franchise")
public class Franchise extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String address;

    @NotNull
    @Column(unique = true)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "franchise")
    private Set<CustomerFranchise> customerFranchises = new HashSet<>();

    @OneToMany(mappedBy = "franchise")
    private Set<Transaction> transactions = new HashSet<>();

}