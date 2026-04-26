package com.rupay.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class TradeDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyRequest {
        @NotBlank(message = "Crypto symbol is required")
        private String cryptoSymbol;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.00000001", message = "Amount must be greater than 0")
        private BigDecimal cryptoAmount;

        @NotBlank(message = "PIN is required")
        @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits")
        private String pin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellRequest {
        @NotBlank(message = "Crypto symbol is required")
        private String cryptoSymbol;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.00000001", message = "Amount must be greater than 0")
        private BigDecimal cryptoAmount;

        @NotBlank(message = "PIN is required")
        @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits")
        private String pin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequest {
        @NotBlank(message = "Crypto symbol is required")
        private String cryptoSymbol;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.00000001", message = "Amount must be greater than 0")
        private BigDecimal cryptoAmount;

        @NotBlank(message = "Recipient email is required")
        @Email(message = "Invalid email format")
        private String recipientEmail;

        @NotBlank(message = "PIN is required")
        @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits")
        private String pin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TradeResponse {
        private Long transactionId;
        private String type;
        private String cryptoSymbol;
        private BigDecimal cryptoAmount;
        private BigDecimal pkrAmount;
        private BigDecimal priceAtTransaction;
        private String status;
        private String message;
    }
}
