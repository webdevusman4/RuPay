package com.rupay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cryptos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Crypto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol; // BTC, ETH, USDT, etc.

    @Column(nullable = false)
    private String name; // Bitcoin, Ethereum, etc.

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal priceInPkr;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal change24h; // Percentage change in 24 hours

    @Column
    private String icon; // Icon URL or identifier

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
