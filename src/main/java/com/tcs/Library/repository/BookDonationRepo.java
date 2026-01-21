package com.tcs.Library.repository;

import com.tcs.Library.entity.BookDonation;
import com.tcs.Library.enums.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookDonationRepo extends JpaRepository<BookDonation, Long> {
    List<BookDonation> findByStatus(DonationStatus status);

    List<BookDonation> findByUserId(Long userId);

    // For dashboard stats
    long countByStatus(DonationStatus status);
}
