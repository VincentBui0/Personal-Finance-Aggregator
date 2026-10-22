package com.example.finance.plaid;

import com.example.finance.user.User;
import com.example.finance.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/plaid")
public class PlaidController {

    private final PlaidService plaidService;
    private final UserRepository userRepository;

    public PlaidController(PlaidService plaidService, UserRepository userRepository) {
        this.plaidService = plaidService;
        this.userRepository = userRepository;
    }

    @PostMapping("/link-token")
    public ResponseEntity<?> createLinkToken(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String token = plaidService.createLinkToken(userDetails.getUsername());
            return ResponseEntity.ok(Map.of("link_token", token));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/exchange-token")
    public ResponseEntity<?> exchangeToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            PlaidItem item = plaidService.exchangePublicToken(body.get("public_token"), user);
            plaidService.syncTransactions(item);
            return ResponseEntity.ok(Map.of("status", "connected"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}