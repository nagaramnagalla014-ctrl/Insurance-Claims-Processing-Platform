package com.insurance.claims.service;

import com.insurance.claims.dto.SettlementRequest;
import com.insurance.claims.exception.ClaimException;
import com.insurance.claims.model.Claim;
import com.insurance.claims.model.Settlement;
import com.insurance.claims.model.User;
import com.insurance.claims.repository.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class SettlementService {

    @Autowired private SettlementRepository settlementRepository;
    @Autowired private ClaimService claimService;

    public Settlement processSettlement(Long claimId, SettlementRequest req, User processor) {
        Claim claim = claimService.getById(claimId);
        if (claim.getStatus() != Claim.ClaimStatus.PENDING_SETTLEMENT) {
            throw new ClaimException("Claim is not pending settlement");
        }

        Settlement settlement = new Settlement();
        settlement.setClaim(claim);
        settlement.setProcessedBy(processor);
        settlement.setSettlementAmount(req.getSettlementAmount());
        settlement.setDeductibleAmount(req.getDeductibleAmount() != null
            ? req.getDeductibleAmount() : BigDecimal.ZERO);
        settlement.setNetPayable(req.getSettlementAmount()
            .subtract(settlement.getDeductibleAmount()));
        settlement.setPaymentMode(Settlement.PaymentMode.valueOf(req.getPaymentMode()));
        settlement.setBeneficiaryName(req.getBeneficiaryName());
        settlement.setBankAccountNumber(req.getBankAccountNumber());
        settlement.setIfscCode(req.getIfscCode());
        settlement.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        settlement.setStatus(Settlement.SettlementStatus.PAID);
        settlement.setPaymentDate(LocalDate.now());
        settlement.setRemarks(req.getRemarks());

        Settlement saved = settlementRepository.save(settlement);
        claim.setApprovedAmount(settlement.getNetPayable());
        claimService.updateStatus(claimId, Claim.ClaimStatus.SETTLED);
        return saved;
    }

    public Settlement rejectClaim(Long claimId, String reason, User processor) {
        Claim claim = claimService.getById(claimId);
        claim.setRejectionReason(reason);
        claimService.updateStatus(claimId, Claim.ClaimStatus.REJECTED);
        return null;
    }

    public Settlement getSettlementForClaim(Long claimId) {
        Claim claim = claimService.getById(claimId);
        return settlementRepository.findByClaim(claim)
            .orElseThrow(() -> new ClaimException("No settlement found"));
    }

    public List<Claim> getPendingSettlements() {
        return claimService.getClaimsByStatus(Claim.ClaimStatus.PENDING_SETTLEMENT);
    }
}
