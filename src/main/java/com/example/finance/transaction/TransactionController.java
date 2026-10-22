package com.example.finance.transaction;

import com.example.finance.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionController(TransactionRepository transactionRepository,
                                 UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDto>> getAll(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String category) {

        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow().getId();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("txnDate").descending());

        Page<TransactionDto> result = category != null
                ? transactionRepository
                    .findByUserIdAndCategoryOrderByTxnDateDesc(userId, category, pageable)
                    .map(t -> new TransactionDto(t.getId(), t.getAmount(),
                            t.getCategory(), t.getMerchant(), t.getTxnDate()))
                : transactionRepository
                    .findByUserIdOrderByTxnDateDesc(userId, pageable)
                    .map(t -> new TransactionDto(t.getId(), t.getAmount(),
                            t.getCategory(), t.getMerchant(), t.getTxnDate()));

        return ResponseEntity.ok(result);
    }
}