package com.onspring.onspring_customer.domain.user.dto;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PointDto implements Serializable {
    Long id;

    PartyDto partyDto;

    EndUserDto endUserDto;

    @NotNull
    BigDecimal amount;

    LocalDateTime validThru;

}
