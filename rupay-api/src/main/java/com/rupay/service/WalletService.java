package com.rupay.service;

import com.rupay.dto.CommonDTO;
import com.rupay.dto.WalletDTO;
import com.rupay.model.*;
import com.rupay.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WithdrawRequestRepository withdrawRequestRepository;
    private final AuthService authService;

    public WalletDTO.WalletResponse getWallet(User user) {
        List<Wallet> wallets = walletRepository.findAllByUser(user);

        List<WalletDTO.CryptoHolding> holdings = wallets.stream()
                .filter(w -> w.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .map(w -> WalletDTO.CryptoHolding.builder()
                        .symbol(w.getCrypto().getSymbol())
                        .name(w.getCrypto().getName())
                        .balance(w.getBalance())
                        .currentPrice(w.getCrypto().getPriceInPkr())
                        .valueInPkr(w.getBalance().multiply(w.getCrypto().getPriceInPkr())
                                .setScale(2, RoundingMode.HALF_UP))
                        .change24h(w.getCrypto().getChange24h())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalCryptoValue = holdings.stream()
                .map(WalletDTO.CryptoHolding::getValueInPkr)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return WalletDTO.WalletResponse.builder()
                .pkrBalance(user.getPkrBalance())
                .cryptoHoldings(holdings)
                .totalValueInPkr(user.getPkrBalance().add(totalCryptoValue))
                .build();
    }

    @Transactional
    public CommonDTO.TransactionResponse deposit(User user, WalletDTO.DepositRequest request) {
        user.setPkrBalance(user.getPkrBalance().add(request.getAmount()));
        userRepository.save(user);

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(Transaction.TransactionType.DEPOSIT)
                .pkrAmount(request.getAmount())
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(transaction);

        return mapToTransactionResponse(transaction);
    }

    @Transactional
    public CommonDTO.TransactionResponse withdraw(User user, WalletDTO.WithdrawRequest request) {
        // Verify PIN
        if (!authService.verifyPin(user, request.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        if (user.getPkrBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct balance immediately (held for withdrawal)
        user.setPkrBalance(user.getPkrBalance().subtract(request.getAmount()));
        userRepository.save(user);

        // Create withdraw request for admin approval
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .user(user)
                .amount(request.getAmount())
                .bankAccount(request.getBankAccount())
                .status(WithdrawRequest.WithdrawStatus.PENDING)
                .build();

        withdrawRequestRepository.save(withdrawRequest);

        // Record transaction as pending
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(Transaction.TransactionType.WITHDRAW)
                .pkrAmount(request.getAmount())
                .bankAccount(request.getBankAccount())
                .status(Transaction.TransactionStatus.PENDING)
                .build();

        transactionRepository.save(transaction);

        return mapToTransactionResponse(transaction);
    }

    public List<CommonDTO.TransactionResponse> getTransactionHistory(User user, String type) {
        List<Transaction> transactions;

        if (type == null || type.equalsIgnoreCase("ALL")) {
            transactions = transactionRepository.findAllByUserOrderByCreatedAtDesc(user);
        } else {
            try {
                Transaction.TransactionType txType = Transaction.TransactionType.valueOf(type.toUpperCase());
                transactions = transactionRepository.findAllByUserAndTypeOrderByCreatedAtDesc(user, txType);
            } catch (IllegalArgumentException e) {
                transactions = transactionRepository.findAllByUserOrderByCreatedAtDesc(user);
            }
        }

        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<CommonDTO.TransactionResponse> getRecentTransactions(User user) {
        return transactionRepository.findTop10ByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private CommonDTO.TransactionResponse mapToTransactionResponse(Transaction tx) {
        return CommonDTO.TransactionResponse.builder()
                .id(tx.getId())
                .type(tx.getType().name())
                .cryptoSymbol(tx.getCrypto() != null ? tx.getCrypto().getSymbol() : null)
                .cryptoName(tx.getCrypto() != null ? tx.getCrypto().getName() : null)
                .cryptoAmount(tx.getCryptoAmount())
                .pkrAmount(tx.getPkrAmount())
                .priceAtTransaction(tx.getPriceAtTransaction())
                .recipientEmail(tx.getRecipientEmail())
                .status(tx.getStatus().name())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
