package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/franchises")
public class UserFranchiseController {

    private final FranchiseService franchiseService;

    @GetMapping("")
    public ResponseEntity<Page<FranchiseDto>> getAllFranchises(
            @PageableDefault(sort = "name", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("pageable: {}", pageable);
        Long userId = SecurityUtil.getCurrentUserId();

        Page<FranchiseDto> result = franchiseService.findFranchiseListByUserId(userId, pageable);

        log.info("result" ,result);

        return ResponseEntity.ok(result);
    }
    @GetMapping("/{franchiseId}")
    public ResponseEntity<FranchiseDto> getFranchiseById(@PathVariable Long franchiseId) {
        log.info("franchiseId: {}", franchiseId);

        FranchiseDto franchise = franchiseService.findFranchiseById(franchiseId);

        return ResponseEntity.ok(franchise);
    }

}
