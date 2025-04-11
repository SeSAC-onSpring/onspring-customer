package com.onspring.onspring_customer.domain.user.dto;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PointDto implements Serializable {
    Long id;

    @NotNull
    PartyDto partyDto;

    EndUserDto endUserDto;

    @NotNull
    BigDecimal assignedAmount;

    @NotNull
    BigDecimal currentAmount;

    LocalDateTime validThru;

    List<EndUserDto> endUserDtoList;

}
