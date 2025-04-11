package com.onspring.onspring_customer.domain.user.dto;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class EndUserDto implements Serializable {
    Long id;

    PartyDto partyDto;

    private List<PartyDto> parties;

    String password;

    @NotNull
    @Size(min = 1)
    String name;

    @NotNull
    @Size(min = 1)
    String phone;

    boolean isActivated;

    private List<Long> partyIds;

    private List<Long> pointIds;
}
