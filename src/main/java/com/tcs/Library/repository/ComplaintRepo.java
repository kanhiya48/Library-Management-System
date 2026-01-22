package com.tcs.Library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.Library.entity.Complaint;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.ComplaintStatus;
import com.tcs.Library.enums.ComplaintCategory;
import java.time.LocalDateTime;

@Repository
public interface ComplaintRepo extends JpaRepository<Complaint, Long> {

        @Query("SELECT c FROM Complaint c WHERE " +
                        "(:searchTerm IS NULL OR LOWER(c.complaintId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(c.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(c.complainant.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
                        "(:category IS NULL OR c.category = :category) AND " +
                        "(:statuses IS NULL OR c.status IN :statuses) AND " +
                        "(:startDate IS NULL OR c.createdAt >= :startDate) AND " +
                        "(:endDate IS NULL OR c.createdAt <= :endDate)")
        Page<Complaint> findAllWithFilters(
                        @Param("searchTerm") String searchTerm,
                        @Param("category") ComplaintCategory category,
                        @Param("statuses") List<ComplaintStatus> statuses,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        Optional<Complaint> findByComplaintId(String complaintId);

        /** Find all complaints by a specific user */
        List<Complaint> findByComplainant(User user);

        Page<Complaint> findByComplainant(User user, Pageable pageable);

        /** Find complaints by user's public ID */
        @Query("SELECT c FROM Complaint c WHERE c.complainant.publicId = :publicId")
        List<Complaint> findByComplainantPublicId(@Param("publicId") java.util.UUID publicId);

        @Query("SELECT c FROM Complaint c WHERE c.complainant.publicId = :publicId")
        Page<Complaint> findByComplainantPublicId(@Param("publicId") java.util.UUID publicId, Pageable pageable);

        /** Find complaints by status */
        List<Complaint> findByStatus(ComplaintStatus status);

        Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable);

        /** Find all pending complaints for auto-assignment */
        List<Complaint> findByStatusOrderByCreatedAtAsc(ComplaintStatus status);

        /** Find complaints assigned to a specific staff member */
        @Query("SELECT c FROM Complaint c WHERE c.assignedStaff = :staff OR c.secondAssignedStaff = :staff")
        List<Complaint> findByAssignedStaff(@Param("staff") User staff);

        @Query("SELECT c FROM Complaint c WHERE c.assignedStaff = :staff OR c.secondAssignedStaff = :staff")
        Page<Complaint> findByAssignedStaff(@Param("staff") User staff, Pageable pageable);

        /** Find complaints escalated to admin */
        List<Complaint> findByStatusIn(List<ComplaintStatus> statuses);

        Page<Complaint> findByStatusIn(List<ComplaintStatus> statuses, Pageable pageable);

        /** Find complaints assigned to a specific admin */
        List<Complaint> findByAssignedAdmin(User admin);

        Page<Complaint> findByAssignedAdmin(User admin, Pageable pageable);

        /** Count pending complaints for dashboard */
        long countByStatus(ComplaintStatus status);

        /** Count complaints by staff */
        @Query("SELECT COUNT(c) FROM Complaint c WHERE (c.assignedStaff = :staff OR c.secondAssignedStaff = :staff) AND c.status IN :statuses")
        long countActiveComplaintsByStaff(@Param("staff") User staff,
                        @Param("statuses") List<ComplaintStatus> statuses);

        /** Count resolved complaints today */
        /** Count resolved complaints today */
        @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = 'RESOLVED' AND c.resolvedAt BETWEEN :startOfDay AND :endOfDay")
        long countResolvedToday(@Param("startOfDay") java.time.LocalDateTime startOfDay,
                        @Param("endOfDay") java.time.LocalDateTime endOfDay);
}
