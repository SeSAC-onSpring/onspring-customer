package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.dto.PointDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface EndUserService {
    Long saveEndUser(EndUserDto endUserDto);

    EndUserDto findEndUserById(Long id);

    List<EndUserDto> findAllEndUser();

    Page<EndUserDto> findAllEndUserByQuery(String name, String partyName, String phone, boolean isActivated,
                                           Pageable pageable);

    Page<PointDto> findPointByEndUserId(Long id, Pageable pageable);

    boolean updateEndUserPasswordById(Long id, String password);

    boolean activateEndUserById(Long id);

    boolean deactivateEndUserById(Long id);
}
