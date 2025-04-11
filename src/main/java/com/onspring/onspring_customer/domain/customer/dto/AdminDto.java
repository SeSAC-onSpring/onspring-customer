package com.onspring.onspring_customer.domain.customer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminDto implements Serializable {
    Long id;

    Long customerId;

    @NotNull
    @Size(min = 1)
    String userName;

    String password;

    boolean isSuperAdmin;

    boolean isActivated;
}
