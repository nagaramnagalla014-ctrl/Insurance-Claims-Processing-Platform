package com.insurance.claims.repository;

import com.insurance.claims.model.Claim;
import com.insurance.claims.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByClaimant(User claimant);
    List<Claim> findByAssignedAdjuster(User adjuster);
    List<Claim> findByStatus(Claim.ClaimStatus status);
    List<Claim> findByClaimantOrderByCreatedAtDesc(User claimant);

    @Query("SELECT c FROM Claim c WHERE c.status IN (:statuses) ORDER BY c.createdAt DESC")
    List<Claim> findByStatusIn(@Param("statuses") List<Claim.ClaimStatus> statuses);

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.status = :status")
    long countByStatus(@Param("status") Claim.ClaimStatus status);
}
