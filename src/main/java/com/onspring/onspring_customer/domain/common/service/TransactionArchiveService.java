package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionArchiveDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionArchiveService {
    List<Integer> closeTransactionById(List<Long> ids);

    Page<TransactionArchiveDto> findAllTransactionArchive(Pageable pageable);
}
