package com.rupay.service;

import com.rupay.dto.AdminDTO;
import com.rupay.model.Transaction;
import com.rupay.model.User;
import com.rupay.model.WithdrawRequest;
import com.rupay.repository.TransactionRepository;
import com.rupay.repository.UserRepository;
import com.rupay.repository.WithdrawRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final WithdrawRequestRepository withdrawRequestRepository;
    private final TransactionRepository transactionRepository;

    public AdminDTO.AdminDashboard getDashboard() {
        List<User> users = userRepository.findAllByIsAdminFalse();
        List<WithdrawRequest> pendingRequests = withdrawRequestRepository
                .findAllByStatusOrderByCreatedAtDesc(WithdrawRequest.WithdrawStatus.PENDING);

        return AdminDTO.AdminDashboard.builder()
                .totalUsers(users.size())
                .pendingWithdrawals(pendingRequests.size())
                .totalDeposits(calculateTotalDeposits())
                .totalWithdrawals(calculateTotalWithdrawals())
                .recentUsers(users.stream()
                        .limit(10)
                        .map(this::mapToUserSummary)
                        .collect(Collectors.toList()))
                .pendingRequests(pendingRequests.stream()
                        .map(this::mapToWithdrawSummary)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<AdminDTO.UserSummary> getAllUsers() {
        return userRepository.findAllByIsAdminFalse()
                .stream()
                .map(this::mapToUserSummary)
                .collect(Collectors.toList());
    }

    public List<AdminDTO.WithdrawRequestSummary> getPendingWithdrawals() {
        return withdrawRequestRepository.findAllByStatusOrderByCreatedAtDesc(WithdrawRequest.WithdrawStatus.PENDING)
                .stream()
                .map(this::mapToWithdrawSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processWithdrawRequest(Long requestId, boolean approve) {
        WithdrawRequest request = withdrawRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Withdraw request not found"));

        if (request.getStatus() != WithdrawRequest.WithdrawStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        User user = request.getUser();

        if (approve) {
            request.setStatus(WithdrawRequest.WithdrawStatus.APPROVED);
            // Update related transaction
            updateWithdrawTransaction(user, request.getAmount(), Transaction.TransactionStatus.COMPLETED);
        } else {
            request.setStatus(WithdrawRequest.WithdrawStatus.REJECTED);
            // Refund the amount
            user.setPkrBalance(user.getPkrBalance().add(request.getAmount()));
            userRepository.save(user);
            // Update related transaction
            updateWithdrawTransaction(user, request.getAmount(), Transaction.TransactionStatus.REJECTED);
        }

        request.setProcessedAt(LocalDateTime.now());
        withdrawRequestRepository.save(request);
    }

    private void updateWithdrawTransaction(User user, BigDecimal amount, Transaction.TransactionStatus status) {
        // Find the pending withdraw transaction and update its status
        transactionRepository.findAllByUserAndTypeOrderByCreatedAtDesc(user, Transaction.TransactionType.WITHDRAW)
                .stream()
                .filter(tx -> tx.getStatus() == Transaction.TransactionStatus.PENDING 
                        && tx.getPkrAmount().compareTo(amount) == 0)
                .findFirst()
                .ifPresent(tx -> {
                    tx.setStatus(status);
                    transactionRepository.save(tx);
                });
    }

    private BigDecimal calculateTotalDeposits() {
        return transactionRepository.findAll().stream()
                .filter(tx -> tx.getType() == Transaction.TransactionType.DEPOSIT 
                        && tx.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .map(Transaction::getPkrAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalWithdrawals() {
        return transactionRepository.findAll().stream()
                .filter(tx -> tx.getType() == Transaction.TransactionType.WITHDRAW 
                        && tx.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .map(Transaction::getPkrAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private AdminDTO.UserSummary mapToUserSummary(User user) {
        return AdminDTO.UserSummary.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .pkrBalance(user.getPkrBalance())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private AdminDTO.WithdrawRequestSummary mapToWithdrawSummary(WithdrawRequest request) {
        return AdminDTO.WithdrawRequestSummary.builder()
                .id(request.getId())
                .userName(request.getUser().getName())
                .userEmail(request.getUser().getEmail())
                .amount(request.getAmount())
                .bankAccount(request.getBankAccount())
                .status(request.getStatus().name())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
