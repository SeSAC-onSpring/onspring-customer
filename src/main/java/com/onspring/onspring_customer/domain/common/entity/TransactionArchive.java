package com.onspring.onspring_customer.domain.common.entity;

import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "transaction_archive")
public class TransactionArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    @NotNull
    private Long transactionCount;

    @NotNull
    private BigDecimal amountSum;

    @NotNull
    private LocalDate duration;
}