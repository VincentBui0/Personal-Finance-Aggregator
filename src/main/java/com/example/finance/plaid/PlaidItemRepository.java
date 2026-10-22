package com.example.finance.plaid;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaidItemRepository extends JpaRepository<PlaidItem, Long> {
    List<PlaidItem> findByUserId(Long userId);
}