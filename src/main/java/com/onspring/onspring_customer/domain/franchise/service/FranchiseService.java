package com.onspring.onspring_customer.domain.franchise.service;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;

import java.util.List;

public interface FranchiseService {
    Long saveFranchise(FranchiseDto franchiseDto);

    FranchiseDto findFranchiseById(Long id);

    List<FranchiseDto> findAllFranchise();

    boolean updateFranchise(FranchiseDto franchiseDto);

    boolean deleteFranchiseById(Long id);
}
