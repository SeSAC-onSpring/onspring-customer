package com.onspring.onspring_customer.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class PointResponseDto {

    BigDecimal availableAmount;
    BigDecimal chargedAmount;

    String partyName;

    boolean sunday;
    boolean monday;
    boolean tuesday;
    boolean wednesday;
    boolean thursday;
    boolean friday;
    boolean saturday;

    boolean isActivated;

    LocalTime allowedTimeStart;
    LocalTime allowedTimeEnd;

    LocalDateTime validThru;
}
