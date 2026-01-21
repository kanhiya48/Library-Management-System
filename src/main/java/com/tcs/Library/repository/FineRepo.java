package com.tcs.Library.repository;

import com.tcs.Library.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FineRepo extends JpaRepository<Fine, Long> {
    List<Fine> findByUserIdAndIsPaid(Long userId, boolean isPaid);

    List<Fine> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.user.id = :userId AND f.isPaid = false")
    BigDecimal getTotalUnpaidFineByUser(Long userId);

    // For dashboard stats
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.isPaid = true")
    BigDecimal getTotalPaidFines();

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.isPaid = false")
    BigDecimal getTotalUnpaidFines();

    long countByIsPaid(boolean isPaid);
}
