package com.onspring.onspring_customer.domain.customer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
public class CustomerDto implements Serializable {
    Long id;

    @NotNull
    @Size(min = 1)
    String name;

    @NotNull
    @Size(min = 1)
    String address;

    @NotNull
    @Size(min = 1)
    String phone;

    boolean isActivated;

    List<Long> adminIds;

    List<Long> partyIds;
}
