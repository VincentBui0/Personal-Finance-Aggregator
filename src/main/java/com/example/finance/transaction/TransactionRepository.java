package com.example.finance.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserIdOrderByTxnDateDesc(Long userId, Pageable pageable);

    Page<Transaction> findByUserIdAndCategoryOrderByTxnDateDesc(
            Long userId, String category, Pageable pageable);

    Optional<Transaction> findByPlaidTxnId(String plaidTxnId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.category = :category " +
           "AND t.txnDate BETWEEN :start AND :end")
    BigDecimal sumByUserIdAndCategoryAndTxnDateBetween(
            @Param("userId") Long userId,
            @Param("category") String category,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}