package com.insurance.claims.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_assessments")
@SequenceGenerator(name = "assess_seq", sequenceName = "CLAIM_ASSESS_SEQ", allocationSize = 1)
public class ClaimAssessment {

    public enum Recommendation { APPROVE_FULL, APPROVE_PARTIAL, REJECT, INVESTIGATE }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assess_seq")
    @Column(name = "assessment_id")
    private Long assessmentId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "claim_id", nullable = false, unique = true)
    private Claim claim;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "adjuster_id", nullable = false)
    private User adjuster;

    @Column(name = "findings", length = 3000)
    private String findings;

    @Column(name = "damage_assessment", length = 1000)
    private String damageAssessment;

    @Column(name = "estimated_loss", precision = 15, scale = 2)
    private BigDecimal estimatedLoss;

    @Column(name = "recommended_amount", precision = 15, scale = 2)
    private BigDecimal recommendedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation", nullable = false)
    private Recommendation recommendation;

    @Column(name = "conditions", length = 1000)
    private String conditions;

    @Column(name = "fraud_indicators", length = 500)
    private String fraudIndicators;

    @Column(name = "assessed_at")
    private LocalDateTime assessedAt;

    @PrePersist
    protected void onCreate() { assessedAt = LocalDateTime.now(); }

    public Long getAssessmentId() { return assessmentId; }
    public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }
    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public User getAdjuster() { return adjuster; }
    public void setAdjuster(User adjuster) { this.adjuster = adjuster; }
    public String getFindings() { return findings; }
    public void setFindings(String findings) { this.findings = findings; }
    public String getDamageAssessment() { return damageAssessment; }
    public void setDamageAssessment(String damageAssessment) { this.damageAssessment = damageAssessment; }
    public BigDecimal getEstimatedLoss() { return estimatedLoss; }
    public void setEstimatedLoss(BigDecimal estimatedLoss) { this.estimatedLoss = estimatedLoss; }
    public BigDecimal getRecommendedAmount() { return recommendedAmount; }
    public void setRecommendedAmount(BigDecimal recommendedAmount) { this.recommendedAmount = recommendedAmount; }
    public Recommendation getRecommendation() { return recommendation; }
    public void setRecommendation(Recommendation recommendation) { this.recommendation = recommendation; }
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    public String getFraudIndicators() { return fraudIndicators; }
    public void setFraudIndicators(String fraudIndicators) { this.fraudIndicators = fraudIndicators; }
    public LocalDateTime getAssessedAt() { return assessedAt; }
    public void setAssessedAt(LocalDateTime assessedAt) { this.assessedAt = assessedAt; }
}
