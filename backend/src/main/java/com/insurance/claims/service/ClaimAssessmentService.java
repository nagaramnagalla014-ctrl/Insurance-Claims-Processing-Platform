package com.insurance.claims.service;

import com.insurance.claims.dto.AssessmentRequest;
import com.insurance.claims.exception.ClaimException;
import com.insurance.claims.model.Claim;
import com.insurance.claims.model.ClaimAssessment;
import com.insurance.claims.model.User;
import com.insurance.claims.repository.ClaimAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClaimAssessmentService {

    @Autowired private ClaimAssessmentRepository assessmentRepository;
    @Autowired private ClaimService claimService;

    public ClaimAssessment submitAssessment(Long claimId, AssessmentRequest req, User adjuster) {
        Claim claim = claimService.getById(claimId);
        if (claim.getStatus() != Claim.ClaimStatus.UNDER_REVIEW
                && claim.getStatus() != Claim.ClaimStatus.ASSESSMENT) {
            throw new ClaimException("Claim is not in a state that allows assessment");
        }

        ClaimAssessment assessment = assessmentRepository.findByClaim(claim)
            .orElse(new ClaimAssessment());
        assessment.setClaim(claim);
        assessment.setAdjuster(adjuster);
        assessment.setFindings(req.getFindings());
        assessment.setDamageAssessment(req.getDamageAssessment());
        assessment.setEstimatedLoss(req.getEstimatedLoss());
        assessment.setRecommendedAmount(req.getRecommendedAmount());
        assessment.setRecommendation(ClaimAssessment.Recommendation.valueOf(req.getRecommendation()));
        assessment.setConditions(req.getConditions());
        assessment.setFraudIndicators(req.getFraudIndicators());

        ClaimAssessment saved = assessmentRepository.save(assessment);
        claimService.updateStatus(claimId, Claim.ClaimStatus.PENDING_SETTLEMENT);
        return saved;
    }

    public ClaimAssessment getAssessmentForClaim(Long claimId) {
        Claim claim = claimService.getById(claimId);
        return assessmentRepository.findByClaim(claim)
            .orElseThrow(() -> new ClaimException("No assessment found for this claim"));
    }

    public List<Claim> getClaimsForAssessment() {
        return claimService.getClaimsByStatus(Claim.ClaimStatus.UNDER_REVIEW);
    }
}
