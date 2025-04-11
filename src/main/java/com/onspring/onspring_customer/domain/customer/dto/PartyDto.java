package com.onspring.onspring_customer.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartyDto implements Serializable {
    Long id;
    Long customerId;
    String name;
    LocalDateTime period;
    BigDecimal amount;
    LocalTime allowedTimeStart;
    LocalTime allowedTimeEnd;
    Long validThru;
    boolean sunday;
    boolean monday;
    boolean tuesday;
    boolean wednesday;
    boolean thursday;
    boolean friday;
    boolean saturday;
    BigDecimal maximumAmount;
    Long maximumTransaction;
    boolean isActivated;

    private List<Long> endUserIds;

    public String getFormattedDays() {
        List<String> enabledDays = new ArrayList<>();
        if (sunday) {
            enabledDays.add("일");
        }
        if (monday) {
            enabledDays.add("월");
        }
        if (tuesday) {
            enabledDays.add("화");
        }
        if (wednesday) {
            enabledDays.add("수");
        }
        if (thursday) {
            enabledDays.add("목");
        }
        if (friday) {
            enabledDays.add("금");
        }
        if (saturday) {
            enabledDays.add("토");
        }

        return String.join(", ", enabledDays);
    }
}
