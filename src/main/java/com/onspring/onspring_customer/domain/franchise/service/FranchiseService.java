package com.onspring.onspring_customer.domain.franchise.service;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FranchiseService {
    Long saveFranchise(FranchiseDto franchiseDto);

    FranchiseDto findFranchiseById(Long id);

    Page<FranchiseDto> findFranchiseListByUserId(Long userId, Pageable pageable);

    List<FranchiseDto> findAllFranchise();

    Page<FranchiseDto> findAllFranchiseByQuery(String userName, String name, String ownerName, String businessNumber,
                                               String address, String phone, String customerName, boolean isActivated
            , Pageable pageable);

    boolean updateFranchise(Long id, FranchiseDto franchiseDto);

    boolean updateFranchisePassword(Long id, String oldPassword, String newPassword);

    boolean activateFranchiseById(Long id);

    List<Boolean> activateFranchiseById(List<Long> ids);

    boolean deactivateFranchiseById(Long id);

    boolean updateMenuImage(FranchiseDto franchiseDto);

    boolean deleteFranchiseById(Long id);

    List<Boolean> deactivateFranchiseById(List<Long> ids);
}
