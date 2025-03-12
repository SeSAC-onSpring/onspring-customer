package com.onspring.onspring_customer.domain.common.entity;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "party_end_user")
public class PartyEndUser extends BaseEntity {
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

}