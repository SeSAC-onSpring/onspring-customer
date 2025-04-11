package com.onspring.onspring_customer.domain.user.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class EndUserPointDto implements Serializable {
    EndUserDto endUserDto;
    PointDto pointDto;
}
