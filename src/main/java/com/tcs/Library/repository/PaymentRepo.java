package com.tcs.Library.repository;

import com.tcs.Library.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    List<Payment> findByUserIdOrderByPaymentDateDesc(Long userId);

    Optional<Payment> findByPaymentId(String paymentId);

    List<Payment> findByUserId(Long userId);
}
