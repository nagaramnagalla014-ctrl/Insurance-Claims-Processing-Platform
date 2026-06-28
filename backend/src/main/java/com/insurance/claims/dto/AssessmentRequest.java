package com.insurance.claims.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AssessmentRequest {
    @NotBlank private String findings;
    private String damageAssessment;
    @NotNull private BigDecimal estimatedLoss;
    @NotNull private BigDecimal recommendedAmount;
    @NotBlank private String recommendation;
    private String conditions;
    private String fraudIndicators;

    public String getFindings() { return findings; }
    public void setFindings(String findings) { this.findings = findings; }
    public String getDamageAssessment() { return damageAssessment; }
    public void setDamageAssessment(String damageAssessment) { this.damageAssessment = damageAssessment; }
    public BigDecimal getEstimatedLoss() { return estimatedLoss; }
    public void setEstimatedLoss(BigDecimal estimatedLoss) { this.estimatedLoss = estimatedLoss; }
    public BigDecimal getRecommendedAmount() { return recommendedAmount; }
    public void setRecommendedAmount(BigDecimal recommendedAmount) { this.recommendedAmount = recommendedAmount; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    public String getFraudIndicators() { return fraudIndicators; }
    public void setFraudIndicators(String fraudIndicators) { this.fraudIndicators = fraudIndicators; }
}
