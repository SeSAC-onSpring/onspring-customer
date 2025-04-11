package com.onspring.onspring_customer.domain.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefreshTokenRequestDto {
    private String refreshToken;
}
