package com.example.finance.plaid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionSyncJob {

    private static final Logger log = LoggerFactory.getLogger(TransactionSyncJob.class);

    private final PlaidItemRepository plaidItemRepository;
    private final PlaidService plaidService;

    public TransactionSyncJob(PlaidItemRepository plaidItemRepository,
                              PlaidService plaidService) {
        this.plaidItemRepository = plaidItemRepository;
        this.plaidService = plaidService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void syncAll() {
        List<PlaidItem> items = plaidItemRepository.findAll();
        log.info("Starting nightly sync for {} Plaid items", items.size());
        items.forEach(item -> {
            try {
                plaidService.syncTransactions(item);
                log.info("Synced item {}", item.getId());
            } catch (Exception e) {
                log.error("Sync failed for item {}: {}", item.getId(), e.getMessage());
            }
        });
    }
}