package com.rupay.repository;

import com.rupay.model.Crypto;
import com.rupay.model.User;
import com.rupay.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findAllByUser(User user);
    Optional<Wallet> findByUserAndCrypto(User user, Crypto crypto);
    List<Wallet> findAllByUserId(Long userId);
}
