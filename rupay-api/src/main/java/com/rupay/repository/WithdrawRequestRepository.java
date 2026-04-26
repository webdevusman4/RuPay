package com.rupay.repository;

import com.rupay.model.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long> {
    List<WithdrawRequest> findAllByStatusOrderByCreatedAtDesc(WithdrawRequest.WithdrawStatus status);
    List<WithdrawRequest> findAllByOrderByCreatedAtDesc();
}
