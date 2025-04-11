package com.onspring.onspring_customer.domain.common.dto;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto implements Serializable {
    Long id;
    FranchiseDto franchiseDto;
    PartyDto partyDto;
    EndUserDto endUserDto;
    LocalDateTime transactionTime;
    BigDecimal amount;
    boolean isAccepted;
    boolean isClosed;
}
