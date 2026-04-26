package com.rupay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "crypto_id")
    private Crypto crypto;

    @Column(precision = 19, scale = 8)
    private BigDecimal cryptoAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal pkrAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal priceAtTransaction;

    @Column
    private String recipientEmail; // For transfer transactions

    @Column
    private String bankAccount; // For withdraw transactions

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.COMPLETED;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TransactionType {
        BUY, SELL, DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, REJECTED
    }
}
