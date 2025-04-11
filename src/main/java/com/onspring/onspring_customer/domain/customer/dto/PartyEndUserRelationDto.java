package com.onspring.onspring_customer.domain.customer.dto;

import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
public class PartyEndUserRelationDto implements Serializable {
    PartyDto partyDto;
    List<EndUserDto> endUserDtoList;
}
