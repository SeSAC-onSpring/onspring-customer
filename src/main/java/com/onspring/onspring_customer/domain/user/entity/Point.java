package com.onspring.onspring_customer.domain.user.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "point")
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;

    @ManyToOne
    @JoinColumn(name = "end_user_id")
    private EndUser endUser;

    @NotNull
    private BigDecimal amount;

    private LocalDateTime validThru;

}