package com.rupay.repository;

import com.rupay.model.Transaction;
import com.rupay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserOrderByCreatedAtDesc(User user);
    List<Transaction> findAllByUserAndTypeOrderByCreatedAtDesc(User user, Transaction.TransactionType type);
    List<Transaction> findTop10ByUserOrderByCreatedAtDesc(User user);
}
