package com.insurance.claims.repository;

import com.insurance.claims.model.Claim;
import com.insurance.claims.model.ClaimAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClaimAssessmentRepository extends JpaRepository<ClaimAssessment, Long> {
    Optional<ClaimAssessment> findByClaim(Claim claim);
}
