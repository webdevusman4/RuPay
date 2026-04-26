package com.rupay.service;

import com.rupay.dto.TradeDTO;
import com.rupay.model.*;
import com.rupay.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final CryptoRepository cryptoRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    @Transactional
    public TradeDTO.TradeResponse buy(User user, TradeDTO.BuyRequest request) {
        // Verify PIN
        if (!authService.verifyPin(user, request.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        Crypto crypto = cryptoRepository.findBySymbol(request.getCryptoSymbol().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Crypto not found"));

        BigDecimal pkrRequired = request.getCryptoAmount()
                .multiply(crypto.getPriceInPkr())
                .setScale(2, RoundingMode.HALF_UP);

        if (user.getPkrBalance().compareTo(pkrRequired) < 0) {
            throw new RuntimeException("Insufficient PKR balance");
        }

        // Deduct PKR
        user.setPkrBalance(user.getPkrBalance().subtract(pkrRequired));
        userRepository.save(user);

        // Add to wallet
        Wallet wallet = walletRepository.findByUserAndCrypto(user, crypto)
                .orElseGet(() -> Wallet.builder()
                        .user(user)
                        .crypto(crypto)
                        .balance(BigDecimal.ZERO)
                        .build());

        wallet.setBalance(wallet.getBalance().add(request.getCryptoAmount()));
        walletRepository.save(wallet);

        // Record transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(Transaction.TransactionType.BUY)
                .crypto(crypto)
                .cryptoAmount(request.getCryptoAmount())
                .pkrAmount(pkrRequired)
                .priceAtTransaction(crypto.getPriceInPkr())
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(transaction);

        return TradeDTO.TradeResponse.builder()
                .transactionId(transaction.getId())
                .type("BUY")
                .cryptoSymbol(crypto.getSymbol())
                .cryptoAmount(request.getCryptoAmount())
                .pkrAmount(pkrRequired)
                .priceAtTransaction(crypto.getPriceInPkr())
                .status("COMPLETED")
                .message("Successfully bought " + request.getCryptoAmount() + " " + crypto.getSymbol())
                .build();
    }

    @Transactional
    public TradeDTO.TradeResponse sell(User user, TradeDTO.SellRequest request) {
        // Verify PIN
        if (!authService.verifyPin(user, request.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        Crypto crypto = cryptoRepository.findBySymbol(request.getCryptoSymbol().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Crypto not found"));

        Wallet wallet = walletRepository.findByUserAndCrypto(user, crypto)
                .orElseThrow(() -> new RuntimeException("You don't have any " + crypto.getSymbol()));

        if (wallet.getBalance().compareTo(request.getCryptoAmount()) < 0) {
            throw new RuntimeException("Insufficient " + crypto.getSymbol() + " balance");
        }

        BigDecimal pkrReceived = request.getCryptoAmount()
                .multiply(crypto.getPriceInPkr())
                .setScale(2, RoundingMode.HALF_UP);

        // Deduct crypto
        wallet.setBalance(wallet.getBalance().subtract(request.getCryptoAmount()));
        walletRepository.save(wallet);

        // Add PKR
        user.setPkrBalance(user.getPkrBalance().add(pkrReceived));
        userRepository.save(user);

        // Record transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(Transaction.TransactionType.SELL)
                .crypto(crypto)
                .cryptoAmount(request.getCryptoAmount())
                .pkrAmount(pkrReceived)
                .priceAtTransaction(crypto.getPriceInPkr())
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(transaction);

        return TradeDTO.TradeResponse.builder()
                .transactionId(transaction.getId())
                .type("SELL")
                .cryptoSymbol(crypto.getSymbol())
                .cryptoAmount(request.getCryptoAmount())
                .pkrAmount(pkrReceived)
                .priceAtTransaction(crypto.getPriceInPkr())
                .status("COMPLETED")
                .message("Successfully sold " + request.getCryptoAmount() + " " + crypto.getSymbol())
                .build();
    }

    @Transactional
    public TradeDTO.TradeResponse transfer(User sender, TradeDTO.TransferRequest request) {
        // Verify PIN
        if (!authService.verifyPin(sender, request.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        User recipient = userRepository.findByEmail(request.getRecipientEmail())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        if (sender.getId().equals(recipient.getId())) {
            throw new RuntimeException("Cannot transfer to yourself");
        }

        Crypto crypto = cryptoRepository.findBySymbol(request.getCryptoSymbol().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Crypto not found"));

        Wallet senderWallet = walletRepository.findByUserAndCrypto(sender, crypto)
                .orElseThrow(() -> new RuntimeException("You don't have any " + crypto.getSymbol()));

        if (senderWallet.getBalance().compareTo(request.getCryptoAmount()) < 0) {
            throw new RuntimeException("Insufficient " + crypto.getSymbol() + " balance");
        }

        // Deduct from sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getCryptoAmount()));
        walletRepository.save(senderWallet);

        // Add to recipient
        Wallet recipientWallet = walletRepository.findByUserAndCrypto(recipient, crypto)
                .orElseGet(() -> Wallet.builder()
                        .user(recipient)
                        .crypto(crypto)
                        .balance(BigDecimal.ZERO)
                        .build());

        recipientWallet.setBalance(recipientWallet.getBalance().add(request.getCryptoAmount()));
        walletRepository.save(recipientWallet);

        BigDecimal valueInPkr = request.getCryptoAmount()
                .multiply(crypto.getPriceInPkr())
                .setScale(2, RoundingMode.HALF_UP);

        // Record sender transaction
        Transaction senderTransaction = Transaction.builder()
                .user(sender)
                .type(Transaction.TransactionType.TRANSFER_OUT)
                .crypto(crypto)
                .cryptoAmount(request.getCryptoAmount())
                .pkrAmount(valueInPkr)
                .priceAtTransaction(crypto.getPriceInPkr())
                .recipientEmail(request.getRecipientEmail())
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(senderTransaction);

        // Record recipient transaction
        Transaction recipientTransaction = Transaction.builder()
                .user(recipient)
                .type(Transaction.TransactionType.TRANSFER_IN)
                .crypto(crypto)
                .cryptoAmount(request.getCryptoAmount())
                .pkrAmount(valueInPkr)
                .priceAtTransaction(crypto.getPriceInPkr())
                .recipientEmail(sender.getEmail())
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(recipientTransaction);

        return TradeDTO.TradeResponse.builder()
                .transactionId(senderTransaction.getId())
                .type("TRANSFER")
                .cryptoSymbol(crypto.getSymbol())
                .cryptoAmount(request.getCryptoAmount())
                .pkrAmount(valueInPkr)
                .priceAtTransaction(crypto.getPriceInPkr())
                .status("COMPLETED")
                .message("Successfully transferred " + request.getCryptoAmount() + " " + crypto.getSymbol() + " to " + request.getRecipientEmail())
                .build();
    }
}
