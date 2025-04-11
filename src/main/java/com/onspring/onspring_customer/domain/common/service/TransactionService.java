package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.SettlmentSummaryDto;
import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Long saveTransaction(Long partyId, TransactionDto transactionDto);
    Long saveFalseTransaction(TransactionDto transactionDto);

    // 미정산 내역 일괄 처리 메소드 추가
    List<Long> saveFalseTransactions(List<Long> transactionIds);

    Long saveTransaction(TransactionDto transactionDto);

    TransactionDto findTransactionById(Long id);

    // 정산처리가 완료된 가맹점별 월별 정리 요약 표시 메소드 추가
    List<TransactionDto> findMonthlySettlementSummary();

    List<TransactionDto> findAllTransaction();

    Page<TransactionDto> findAllAcceptedAndNotClosedTransaction(Pageable pageable);

    Page<TransactionDto> findAllTransactionByQuery(String by, String name, LocalDate after,
                                                   LocalDate before, Pageable pageable);

    List<TransactionDto> findTransactionByFranchiseId(Long franchiseId,
                                                LocalDateTime startDate,
                                                LocalDateTime endDate,
                                                String period);
    Page<TransactionDto> findTransactionByEndUserId(Long userId, Pageable pageable);

    List<TransactionDto> findSettlementByFranchiseId(Long franchiseId,
                                                     String month,
                                                     String period,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate);

    List<SettlmentSummaryDto> getMonthlySettlementSummaries(Long franchiseId);

    boolean cancelTransaction(Long franchiseId, Long transactionId);
}
