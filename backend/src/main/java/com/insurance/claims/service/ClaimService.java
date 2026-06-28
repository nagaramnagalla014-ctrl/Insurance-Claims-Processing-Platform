package com.insurance.claims.service;

import com.insurance.claims.dto.ClaimRequest;
import com.insurance.claims.exception.ClaimException;
import com.insurance.claims.model.*;
import com.insurance.claims.repository.ClaimRepository;
import com.insurance.claims.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ClaimService {

    @Autowired private ClaimRepository claimRepository;
    @Autowired private PolicyRepository policyRepository;
    @Autowired private ClaimSearchService searchService;

    public Claim submitClaim(ClaimRequest req, User claimant) {
        Policy policy = policyRepository.findById(req.getPolicyId())
            .orElseThrow(() -> new ClaimException("Policy not found"));

        if (!policy.getPolicyholder().getUserId().equals(claimant.getUserId())) {
            throw new ClaimException("Policy does not belong to this user");
        }
        if (policy.getStatus() != Policy.PolicyStatus.ACTIVE) {
            throw new ClaimException("Policy is not active");
        }

        Claim claim = new Claim();
        claim.setClaimNumber("CLM-" + System.currentTimeMillis());
        claim.setPolicy(policy);
        claim.setClaimant(claimant);
        claim.setClaimType(Claim.ClaimType.valueOf(req.getClaimType()));
        claim.setIncidentDate(req.getIncidentDate());
        claim.setIncidentLocation(req.getIncidentLocation());
        claim.setDescription(req.getDescription());
        claim.setClaimedAmount(req.getClaimedAmount());
        claim.setStatus(Claim.ClaimStatus.SUBMITTED);
        claim.setSubmittedAt(LocalDateTime.now());

        Claim saved = claimRepository.save(claim);
        searchService.indexClaim(saved);
        return saved;
    }

    public List<Claim> getClaimsForUser(User user) {
        return claimRepository.findByClaimantOrderByCreatedAtDesc(user);
    }

    public Claim getById(Long id) {
        return claimRepository.findById(id)
            .orElseThrow(() -> new ClaimException("Claim not found: " + id));
    }

    public Claim getByClaimNumber(String number) {
        return claimRepository.findByClaimNumber(number)
            .orElseThrow(() -> new ClaimException("Claim not found: " + number));
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public List<Claim> getClaimsByStatus(Claim.ClaimStatus status) {
        return claimRepository.findByStatus(status);
    }

    public List<Claim> getClaimsForAdjuster(User adjuster) {
        return claimRepository.findByAssignedAdjuster(adjuster);
    }

    public List<Claim> getOpenClaims() {
        return claimRepository.findByStatusIn(Arrays.asList(
            Claim.ClaimStatus.SUBMITTED, Claim.ClaimStatus.UNDER_REVIEW, Claim.ClaimStatus.ASSESSMENT
        ));
    }

    public Claim assignAdjuster(Long claimId, User adjuster) {
        Claim claim = getById(claimId);
        claim.setAssignedAdjuster(adjuster);
        claim.setStatus(Claim.ClaimStatus.UNDER_REVIEW);
        Claim saved = claimRepository.save(claim);
        searchService.indexClaim(saved);
        return saved;
    }

    public Claim updateStatus(Long claimId, Claim.ClaimStatus newStatus) {
        Claim claim = getById(claimId);
        claim.setStatus(newStatus);
        if (newStatus == Claim.ClaimStatus.SETTLED || newStatus == Claim.ClaimStatus.REJECTED
                || newStatus == Claim.ClaimStatus.CLOSED) {
            claim.setResolvedAt(LocalDateTime.now());
        }
        Claim saved = claimRepository.save(claim);
        searchService.indexClaim(saved);
        return saved;
    }
}
