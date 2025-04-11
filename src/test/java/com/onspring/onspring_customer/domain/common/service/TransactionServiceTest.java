package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService; // 실제 서비스 주입

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * 특정 가맹점의 결제내역 period 조회 테스트
     */
    @Test
    public void testFindTransactionByFranchiseIdWithPeriod() {
        Long franchiseId = 1L;
        String period = "최근1주";  // 예: 최근 1주간


        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        switch (period) {
            case "오늘":
                startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);  // 오늘의 시작 시점
                endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);  // 오늘의 끝 시점
                break;
            case "최근1주":
                startDate = LocalDateTime.now().minusWeeks(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                System.out.println(startDate + " - " + endDate);
                break;
            case "최근2주":
                startDate = LocalDateTime.now().minusWeeks(2).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                break;
            case "최근3주":
                startDate = LocalDateTime.now().minusWeeks(3).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                break;
            default:
                throw new IllegalArgumentException("Invalid period value: " + period);
        }

        List<TransactionDto> transactions = transactionService.findTransactionByFranchiseId(franchiseId, startDate, endDate, null);
        System.out.println(transactions);
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty(), "No transactions found for the given period.");
    }

    /**
     * 특정 가맹점의 결제내역 기간 조회 테스트
     */
    @Test
    public void testFindTransactionByFranchiseIdWithDateRange() {
        Long franchiseId = 1L;


        LocalDateTime startDate = LocalDateTime.now().minusDays(3).withHour(0).withMinute(0).withSecond(0).withNano(0);  // 예: 3일전
        LocalDateTime endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);  // 예: 오늘

        List<TransactionDto> transactions = transactionService.findTransactionByFranchiseId(franchiseId, startDate, endDate, null);

        System.out.println(transactions);
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty(), "No transactions found for the given date range.");
    }

    /**
     * 특정 트랜잭션 취소 테스트 (이미 DB에 있는 트랜잭션 데이터 사용)
     */
    @Test
    public void testCancelTransaction() {
        // 이미 DB에 있는 트랜잭션 ID와 가맹점 ID
        Long franchiseId = 1L;
        Long transactionId = 3L;

        // 취소 메서드 실행
        boolean result = transactionService.cancelTransaction(franchiseId, transactionId);

        assert result;

    }

    /**
     * 이미 정산된 내역에 대한 결제 취소 테스트
     */
    @Transactional
    @Test
    public void testCancelTransactionIsClosed() {
        // 실제 DB에 있는 franchiseId와 transactionId 사용 (존재하는 데이터여야 함)
        Long franchiseId = 1L;
        Long transactionId = 286L;

        // 예외 발생 여부 테스트
        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.cancelTransaction(franchiseId, transactionId);
        });

        // 예외 메시지가 "Cannot cancel a closed transaction"인지 확인
        assertEquals("Cannot cancel a closed transaction", exception.getMessage());
    }

    @Test
    public void testFindSettlementByFranchiseIdFor2025March() {
        // 2025년 3월 1일부터 15일까지 조회
        String month = null;
        String period = null;  // period는 사용하지 않음
        String customStartDate = "2025-03-15";  // 2025년 3월 1일
        String customEndDate = "2025-03-15";  // 2025년 3월 15일

        List<TransactionDto> transactions = transactionService.findSettlementByFranchiseId(1L, month, period, customStartDate, customEndDate);

        System.out.println(transactions);
        assertNotNull(transactions);
        assertTrue(transactions.size() > 0, "No transactions found for the given date range");
    }
    @Test
    public void testFindSettlementByFranchiseIdForLastWeek() {
        // 최근 1주 조회
        String month = null;
        String period = "최근1주"; // 최근 1주
        String customStartDate = null;
        String customEndDate = null;

        List<TransactionDto> transactions = transactionService.findSettlementByFranchiseId(1L, month, period, customStartDate, customEndDate);

        System.out.println(transactions);
        assertNotNull(transactions);
        assertTrue(transactions.size() > 0, "No transactions found for the given date range");
    }

    @Test
    public void testFindSettlementByFranchiseIdForMonth() {
        // 최근 1주 조회
        String month = "2025-03";
        String period = null; // 최근 1주
        String customStartDate = null;
        String customEndDate = null;

        List<TransactionDto> transactions = transactionService.findSettlementByFranchiseId(1L, month, period, customStartDate, customEndDate);

        System.out.println(transactions);
        assertNotNull(transactions);
        assertTrue(transactions.size() > 0, "No transactions found for the given date range");
    }



}
