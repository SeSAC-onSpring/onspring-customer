package com.onspring.onspring_customer.domain.common.repository;

import com.onspring.onspring_customer.domain.common.entity.TransactionArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface TransactionArchiveRepository extends JpaRepository<TransactionArchive, Long> {
    List<TransactionArchive> findByDurationIn(@NonNull Collection<LocalDate> durations);
}