package com.onspring.onspring_customer.domain.customer.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "admin")
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @Column(unique = true)
    private String userName;

    @NotNull
    private boolean isSuperAdmin;

}