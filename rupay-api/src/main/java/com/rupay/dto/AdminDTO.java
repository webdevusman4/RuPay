package com.rupay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;
        private BigDecimal pkrBalance;
        private boolean isActive;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawRequestSummary {
        private Long id;
        private String userName;
        private String userEmail;
        private BigDecimal amount;
        private String bankAccount;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminDashboard {
        private int totalUsers;
        private int pendingWithdrawals;
        private BigDecimal totalDeposits;
        private BigDecimal totalWithdrawals;
        private List<UserSummary> recentUsers;
        private List<WithdrawRequestSummary> pendingRequests;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessWithdrawRequest {
        private boolean approve;
    }
}
