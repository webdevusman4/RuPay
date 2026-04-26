package com.rupay.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class WalletDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepositRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "100", message = "Minimum deposit is PKR 100")
        private BigDecimal amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "100", message = "Minimum withdrawal is PKR 100")
        private BigDecimal amount;

        @NotBlank(message = "Bank account is required")
        private String bankAccount;

        @NotBlank(message = "PIN is required")
        @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits")
        private String pin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletResponse {
        private BigDecimal pkrBalance;
        private List<CryptoHolding> cryptoHoldings;
        private BigDecimal totalValueInPkr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CryptoHolding {
        private String symbol;
        private String name;
        private BigDecimal balance;
        private BigDecimal currentPrice;
        private BigDecimal valueInPkr;
        private BigDecimal change24h;
    }
}
