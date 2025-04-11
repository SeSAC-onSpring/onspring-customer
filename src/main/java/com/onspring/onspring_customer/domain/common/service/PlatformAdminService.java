package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.PlatformAdminDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlatformAdminService {
    Long savePlatformAdmin(PlatformAdminDto platformAdminDto);

    PlatformAdminDto findPlatformAdminById(Long id);

    List<PlatformAdminDto> findAllPlatformAdmin();

    Page<PlatformAdminDto> findAllPlatformAdminByQuery(String userName, boolean isActivated, Pageable pageable);

    boolean updatePlatformAdminPasswordById(Long id, String password);

    boolean activatePlatformAdminById(Long id);

    boolean deactivatePlatformAdminById(Long id);

    boolean deletePlatformAdminById(Long id);
}
