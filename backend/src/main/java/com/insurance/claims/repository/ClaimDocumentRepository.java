package com.insurance.claims.repository;

import com.insurance.claims.model.Claim;
import com.insurance.claims.model.ClaimDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, Long> {
    List<ClaimDocument> findByClaim(Claim claim);
    List<ClaimDocument> findByClaimAndVerificationStatus(Claim claim, ClaimDocument.VerificationStatus status);
}
