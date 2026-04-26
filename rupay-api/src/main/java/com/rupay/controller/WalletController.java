package com.rupay.controller;

import com.rupay.dto.CommonDTO;
import com.rupay.dto.WalletDTO;
import com.rupay.model.User;
import com.rupay.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<CommonDTO.ApiResponse<WalletDTO.WalletResponse>> getWallet(
            @AuthenticationPrincipal User user) {
        WalletDTO.WalletResponse response = walletService.getWallet(user);
        return ResponseEntity.ok(CommonDTO.ApiResponse.success(response));
    }

    @PostMapping("/deposit")
    public ResponseEntity<CommonDTO.ApiResponse<CommonDTO.TransactionResponse>> deposit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WalletDTO.DepositRequest request) {
        try {
            CommonDTO.TransactionResponse response = walletService.deposit(user, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Deposit successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<CommonDTO.ApiResponse<CommonDTO.TransactionResponse>> withdraw(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WalletDTO.WithdrawRequest request) {
        try {
            CommonDTO.TransactionResponse response = walletService.withdraw(user, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(
                    "Withdrawal request submitted. Awaiting admin approval.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<CommonDTO.ApiResponse<List<CommonDTO.TransactionResponse>>> getTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String type) {
        List<CommonDTO.TransactionResponse> transactions = walletService.getTransactionHistory(user, type);
        return ResponseEntity.ok(CommonDTO.ApiResponse.success(transactions));
    }

    @GetMapping("/transactions/recent")
    public ResponseEntity<CommonDTO.ApiResponse<List<CommonDTO.TransactionResponse>>> getRecentTransactions(
            @AuthenticationPrincipal User user) {
        List<CommonDTO.TransactionResponse> transactions = walletService.getRecentTransactions(user);
        return ResponseEntity.ok(CommonDTO.ApiResponse.success(transactions));
    }
}
