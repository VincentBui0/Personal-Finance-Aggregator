package com.example.finance.plaid;

import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import com.example.finance.transaction.Transaction;
import com.example.finance.transaction.TransactionRepository;
import com.example.finance.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PlaidService {

    private static final Logger log = LoggerFactory.getLogger(PlaidService.class);

    private final PlaidApi plaidClient;
    private final PlaidItemRepository plaidItemRepository;
    private final TransactionRepository transactionRepository;

    @Value("${app.plaid.client-id}")
    private String clientId;

    @Value("${app.plaid.secret}")
    private String secret;

    public PlaidService(PlaidApi plaidClient,
                        PlaidItemRepository plaidItemRepository,
                        TransactionRepository transactionRepository) {
        this.plaidClient = plaidClient;
        this.plaidItemRepository = plaidItemRepository;
        this.transactionRepository = transactionRepository;
    }

    public String createLinkToken(String userId) throws IOException {
        LinkTokenCreateRequest req = new LinkTokenCreateRequest()
                .clientId(clientId)
                .secret(secret)
                .clientName("Finance App")
                .user(new LinkTokenCreateRequestUser().clientUserId(userId))
                .products(List.of(Products.TRANSACTIONS))
                .countryCodes(List.of(CountryCode.US))
                .language("en");
        Response<LinkTokenCreateResponse> resp = plaidClient.linkTokenCreate(req).execute();
        return resp.body().getLinkToken();
    }

    public PlaidItem exchangePublicToken(String publicToken, User user) throws IOException {
        ItemPublicTokenExchangeRequest req = new ItemPublicTokenExchangeRequest()
                .clientId(clientId)
                .secret(secret)
                .publicToken(publicToken);
        Response<ItemPublicTokenExchangeResponse> resp =
                plaidClient.itemPublicTokenExchange(req).execute();

        PlaidItem item = new PlaidItem();
        item.setUser(user);
        item.setAccessToken(resp.body().getAccessToken());
        item.setItemId(resp.body().getItemId());
        return plaidItemRepository.save(item);
    }

    public void syncTransactions(PlaidItem item) {
        try {
            TransactionsSyncRequest req = new TransactionsSyncRequest()
                    .clientId(clientId)
                    .secret(secret)
                    .accessToken(item.getAccessToken());
            Response<TransactionsSyncResponse> resp =
                    plaidClient.transactionsSync(req).execute();

            resp.body().getAdded().forEach(t -> {
                if (transactionRepository.findByPlaidTxnId(t.getTransactionId()).isEmpty()) {
                    Transaction txn = new Transaction();
                    txn.setUser(item.getUser());
                    txn.setPlaidTxnId(t.getTransactionId());
                    txn.setAmount(BigDecimal.valueOf(t.getAmount()));
                    txn.setMerchant(t.getMerchantName());
                    txn.setCategory(t.getPersonalFinanceCategory() != null
                            ? t.getPersonalFinanceCategory().getPrimary() : "Other");
                    txn.setTxnDate(LocalDate.parse(t.getDate().toString()));
                    transactionRepository.save(txn);
                }
            });
        } catch (Exception e) {
            log.error("Sync failed for item {}: {}", item.getId(), e.getMessage());
        }
    }
}