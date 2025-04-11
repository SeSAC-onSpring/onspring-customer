package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.service.TransactionService;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import com.onspring.onspring_customer.domain.user.service.PointService;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/user/transactions")
@RestController
public class UserTransactionController {

    private final TransactionService transactionService;
    private final PointService pointService;
    private final FranchiseService franchiseService;
    private final EndUserService endUserService;


    //사용자의 결제내역 출력
    @GetMapping("")
    public ResponseEntity<Page<TransactionDto>> getUserTransactions(
            @PageableDefault(sort = "transactionTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info(pageable);
        Long userId = SecurityUtil.getCurrentUserId(); // 여기서 userId 추출
        Page<TransactionDto> result = transactionService.findTransactionByEndUserId(userId, pageable);
        return ResponseEntity.ok(result);
    }
    //사용자의 가맹점 결제
    @PostMapping("/{franchiseId}/point/{pointId}")
    public ResponseEntity<Long> addTransaction(@PathVariable Long franchiseId,
                                               @PathVariable Long pointId,
                                               @RequestParam Long partyId,
                                               @RequestBody TransactionDto transactionDto) {

        Long userId = SecurityUtil.getCurrentUserId();

        log.info(transactionDto);
        log.info(franchiseId);
        FranchiseDto franchiseDto = franchiseService.findFranchiseById(franchiseId);

        //MultiPart필드를 null로 초기화
        franchiseDto.setFiles(new ArrayList<>());

        log.info("franchiseDto: " + franchiseDto);

        transactionDto.setFranchiseDto(franchiseDto);

        log.info(endUserService.findEndUserById(userId));
        transactionDto.setEndUserDto(endUserService.findEndUserById(userId));


        // 트랜잭션 저장 서비스 호출
        Long savedTransactionId = transactionService.saveTransaction(partyId, transactionDto);

        //결제 후 가용 포인트 처리 로직
        pointService.usePointOnPayment(pointId, transactionDto.getAmount());

        // 저장된 트랜잭션 ID 반환
        return ResponseEntity.ok(savedTransactionId);
    }
}
