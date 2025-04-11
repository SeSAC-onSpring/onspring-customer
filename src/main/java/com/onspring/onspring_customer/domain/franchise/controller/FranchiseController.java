package com.onspring.onspring_customer.domain.franchise.controller;

import com.onspring.onspring_customer.domain.common.dto.SettlmentSummaryDto;
import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.service.TransactionService;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import com.onspring.onspring_customer.domain.auth.dto.PasswordUpdateRequest;
import com.onspring.onspring_customer.global.util.file.CustomFileUtil;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/franchise")
public class FranchiseController {
    private final FranchiseService franchiseService;
    private final TransactionService transactionService;
    private final CustomFileUtil customFileUtil;

    //프랜차이즈 정보 보기
    @GetMapping("/info")
    public ResponseEntity<FranchiseDto> getFranchiseInfo() {
        Long franchiseId = SecurityUtil.getCurrentUserId();

        FranchiseDto franchiseDto = franchiseService.findFranchiseById(franchiseId);

        return ResponseEntity.ok(franchiseDto);
    }


    // 가맹점 정보 업데이트
    @PutMapping("/info")
    public ResponseEntity<String> updateFranchise(@RequestBody FranchiseDto franchiseDto) {
        Long franchiseId = SecurityUtil.getCurrentUserId();
        boolean isUpdated = franchiseService.updateFranchise(franchiseId, franchiseDto);

        if (isUpdated) {
            return ResponseEntity.ok("Franchise updated successfully");
        } else {
            return ResponseEntity.status(400).body("Failed to update franchise");
        }
    }

    @PutMapping("/password")
    public ResponseEntity<String> updateFranchisePassword(
            @RequestBody PasswordUpdateRequest request) {

        log.info("Franchise password update request: {}", request);
        Long franchiseId = SecurityUtil.getCurrentUserId();

        boolean success = franchiseService.updateFranchisePassword(franchiseId, request.getOldPassword(), request.getNewPassword());

        if (success) {
            log.info("Franchise password updated successfully");
            return ResponseEntity.ok("password updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password update failed");
        }
    }


    //메뉴 사진 업로드
    @PutMapping(value = "/menu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMenu(@ModelAttribute FranchiseDto franchiseDto) {
        Long franchiseId = SecurityUtil.getCurrentUserId();

        FranchiseDto oldFranchiseDto = franchiseService.findFranchiseById(franchiseId);
        log.info(oldFranchiseDto);

        //기존 데이터베이스에 존재하는 파일들
        List<String> oldFileNames = oldFranchiseDto.getUploadFileNames();

        //새로 업로드 해야하는 파일
        List<MultipartFile> files = franchiseDto.getFiles();

        log.info("files : " + files);



        //새로 업로드 될 파일 이름들
        List<String> currentFileNames = customFileUtil.saveFiles(files);

        //화면에서 유지할 파일들
        List<String> uploadFileNames = franchiseDto.getUploadFileNames();

        //유지될 파일들 + 새로만든 파일 이름들
        if(currentFileNames != null && currentFileNames.size() > 0) {
            uploadFileNames.addAll(currentFileNames);
        }

        franchiseService.updateMenuImage(franchiseDto);

        log.info(oldFileNames);

        if(oldFileNames != null && !oldFileNames.isEmpty()){
            //지울 파일 목록 찾기
            //예전 파일 이름 중에서 지워져야 할 파일 이름들
            //기존에 있던 파일 이름들 중에 새로 업로드될 파일 이름에 없는 파일들 remove
            List<String> removeFiles = oldFileNames
                    .stream()
                    .filter(fileName -> !uploadFileNames.contains(fileName)).collect(Collectors.toList());

            customFileUtil.deleteFiles(removeFiles);
            log.info("files : " + removeFiles);
        }

        return ResponseEntity.ok("메뉴 이미지 업데이트가 완료되었습니다.");
    }

    //메뉴 사진 조회
    @GetMapping("/menu/{fileName}")
    public ResponseEntity<Resource> getMenu(@PathVariable String fileName) {
        return customFileUtil.getFile(fileName);
    }

    //정산 요약 조회
    @GetMapping("/settlements")
    public ResponseEntity<List<SettlmentSummaryDto>> getSettlementsSummary() {
        Long franchiseId = SecurityUtil.getCurrentUserId();

        List<SettlmentSummaryDto> settlementSummaryDto = transactionService.getMonthlySettlementSummaries(franchiseId);

        log.info("settlementSummaryDto : " + settlementSummaryDto);

        return ResponseEntity.ok(settlementSummaryDto);

    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getFranchiseTransactions(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Long franchiseId = SecurityUtil.getCurrentUserId();

        // period가 제공된 경우, 이를 사용(날짜 매개변수 무시)
        if (period != null && !period.isEmpty()) {
            List<TransactionDto> transactionDtoList = transactionService.findTransactionByFranchiseId(franchiseId, null, null, period);
            return ResponseEntity.ok(transactionDtoList);
        }

        // 그렇지 않으면 날짜 매개변수 사용
        ZonedDateTime startDateTime = null;
        ZonedDateTime endDateTime = null;

        // 제공된 경우 startDate 파싱
        if (startDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
                startDateTime = startLocalDate.atTime(0, 0, 0, 0).atZone(ZoneId.of("Asia/Seoul"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. 'yyyy.MM.dd' 형식이어야 합니다.");
            }
        }

        // 제공된 경우 endDate 파싱
        if (endDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                LocalDate endLocalDate = LocalDate.parse(endDate, formatter);
                endDateTime = endLocalDate.atTime(23, 59, 59, 999999999).atZone(ZoneId.of("Asia/Seoul"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. 'yyyy.MM.dd' 형식이어야 합니다.");
            }
        }

        // 날짜와 null을 period로 서비스 호출
        List<TransactionDto> transactionDtoList = transactionService.findTransactionByFranchiseId(
                franchiseId,
                startDateTime != null ? startDateTime.toLocalDateTime() : null,
                endDateTime != null ? endDateTime.toLocalDateTime() : null,
                null
        );

        return ResponseEntity.ok(transactionDtoList);
    }

    @GetMapping("/settlements/{month}")
    public ResponseEntity<List<TransactionDto>> getFranchiseSettlements(
            @PathVariable String month,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Long franchiseId = SecurityUtil.getCurrentUserId();

        // period가 제공된 경우, 이를 사용(날짜 매개변수 무시)
        if (period != null && !period.isEmpty()) {
            List<TransactionDto> settlementDtoList = transactionService.findSettlementByFranchiseId(franchiseId, month, period, null, null);
            return ResponseEntity.ok(settlementDtoList);
        }

        // 그렇지 않으면 날짜 매개변수 사용
        ZonedDateTime startDateTime = null;
        ZonedDateTime endDateTime = null;

        // 제공된 경우 startDate 파싱
        if (startDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
                startDateTime = startLocalDate.atTime(0, 0, 0, 0).atZone(ZoneId.of("Asia/Seoul"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. 'yyyy.MM.dd' 형식이어야 합니다.");
            }
        }

        // 제공된 경우 endDate 파싱
        if (endDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                LocalDate endLocalDate = LocalDate.parse(endDate, formatter);
                endDateTime = endLocalDate.atTime(23, 59, 59, 999999999).atZone(ZoneId.of("Asia/Seoul"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. 'yyyy.MM.dd' 형식이어야 합니다.");
            }
        }

        // 날짜와 null을 period로 서비스 호출
        List<TransactionDto> settlementDtoList = transactionService.findSettlementByFranchiseId(
                franchiseId,
                month,
                period,
                startDateTime != null ? startDateTime.toLocalDateTime() : null,
                endDateTime != null ? endDateTime.toLocalDateTime() : null
        );

        return ResponseEntity.ok(settlementDtoList);
    }

    @PutMapping("/transactions/{transactionId}/cancel")
    public ResponseEntity<String> cancelTransaction(@PathVariable Long transactionId) {
        log.info(transactionId);
        Long franchiseId = SecurityUtil.getCurrentUserId();
        try {
            boolean isCancelled = transactionService.cancelTransaction(franchiseId, transactionId);
            if (isCancelled) {
                return ResponseEntity.ok("Transaction successfully cancelled.");
            } else {
                return ResponseEntity.status(400).body("Failed to cancel the transaction.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }
}
