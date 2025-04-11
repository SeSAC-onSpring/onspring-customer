package com.onspring.onspring_customer.domain.customer.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.user.entity.Point;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "party")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Party extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @Column(unique = true)
    private String name;

    private LocalDateTime period;

    private BigDecimal amount;

    private LocalTime allowedTimeStart;

    private LocalTime allowedTimeEnd;

    private Long validThru;

    @NotNull
    private boolean sunday;

    @NotNull
    private boolean monday;

    @NotNull
    private boolean tuesday;

    @NotNull
    private boolean wednesday;

    @NotNull
    private boolean thursday;

    @NotNull
    private boolean friday;

    @NotNull
    private boolean saturday;

    private BigDecimal maximumAmount;

    private Long maximumTransaction;

    @NotNull
    private boolean isActivated;

    @OneToMany(mappedBy = "party")
    private List<Point> points = new ArrayList<>();

    @OneToMany(mappedBy = "party")
    private List<Transaction> transactions = new ArrayList<>();

}