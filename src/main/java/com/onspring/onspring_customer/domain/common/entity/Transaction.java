package com.onspring.onspring_customer.domain.common.entity;

import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    @ManyToOne
    @JoinColumn(name = "end_user_id")
    private EndUser endUser;

    @NotNull
    private LocalDateTime transactionTime;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private boolean isAccepted;

    @NotNull
    private boolean isClosed;

}