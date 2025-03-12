package com.onspring.onspring_customer.domain.customer.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
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
@Table(name = "customer")
public class Customer extends BaseEntity {
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

    @NotNull
    boolean isActivated;

    @OneToMany(mappedBy = "customer")
    private Set<Admin> admins = new HashSet<>();

    @OneToMany(mappedBy = "customer")
    private Set<CustomerFranchise> customerFranchises = new HashSet<>();

    @OneToMany(mappedBy = "customer")
    private Set<Party> parties = new HashSet<>();

}