package com.rupay.config;

import com.rupay.model.Crypto;
import com.rupay.model.User;
import com.rupay.model.Wallet;
import com.rupay.repository.CryptoRepository;
import com.rupay.repository.UserRepository;
import com.rupay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CryptoRepository cryptoRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeCryptos();
        initializeUsers();
        log.info("Demo data initialized successfully");
    }

    private void initializeCryptos() {
        if (cryptoRepository.count() == 0) {
            List<Crypto> cryptos = List.of(
                    Crypto.builder()
                            .symbol("BTC")
                            .name("Bitcoin")
                            .priceInPkr(new BigDecimal("25430000"))
                            .change24h(new BigDecimal("2.5"))
                            .icon("bitcoin")
                            .isActive(true)
                            .build(),
                    Crypto.builder()
                            .symbol("ETH")
                            .name("Ethereum")
                            .priceInPkr(new BigDecimal("890000"))
                            .change24h(new BigDecimal("-1.2"))
                            .icon("ethereum")
                            .isActive(true)
                            .build(),
                    Crypto.builder()
                            .symbol("USDT")
                            .name("Tether")
                            .priceInPkr(new BigDecimal("279"))
                            .change24h(new BigDecimal("0.1"))
                            .icon("tether")
                            .isActive(true)
                            .build(),
                    Crypto.builder()
                            .symbol("BNB")
                            .name("BNB")
                            .priceInPkr(new BigDecimal("168500"))
                            .change24h(new BigDecimal("3.8"))
                            .icon("bnb")
                            .isActive(true)
                            .build(),
                    Crypto.builder()
                            .symbol("XRP")
                            .name("XRP")
                            .priceInPkr(new BigDecimal("145"))
                            .change24h(new BigDecimal("-0.5"))
                            .icon("xrp")
                            .isActive(true)
                            .build()
            );

            cryptoRepository.saveAll(cryptos);
            log.info("Initialized {} cryptocurrencies", cryptos.size());
        }
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            // Create admin user
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@rupay.com")
                    .password(passwordEncoder.encode("admin123"))
                    .pin("0000")
                    .pkrBalance(BigDecimal.ZERO)
                    .isAdmin(true)
                    .isActive(true)
                    .build();

            userRepository.save(admin);
            log.info("Created admin user: admin@rupay.com");

            // Create demo user
            User demoUser = User.builder()
                    .name("Ahmed Khan")
                    .email("ahmed@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .pin("1234")
                    .pkrBalance(new BigDecimal("150000"))
                    .isAdmin(false)
                    .isActive(true)
                    .build();

            userRepository.save(demoUser);

            // Add some crypto to demo user's wallet
            Crypto btc = cryptoRepository.findBySymbol("BTC").orElse(null);
            Crypto eth = cryptoRepository.findBySymbol("ETH").orElse(null);

            if (btc != null) {
                Wallet btcWallet = Wallet.builder()
                        .user(demoUser)
                        .crypto(btc)
                        .balance(new BigDecimal("0.005"))
                        .build();
                walletRepository.save(btcWallet);
            }

            if (eth != null) {
                Wallet ethWallet = Wallet.builder()
                        .user(demoUser)
                        .crypto(eth)
                        .balance(new BigDecimal("0.25"))
                        .build();
                walletRepository.save(ethWallet);
            }

            log.info("Created demo user: ahmed@example.com");
        }
    }
}
