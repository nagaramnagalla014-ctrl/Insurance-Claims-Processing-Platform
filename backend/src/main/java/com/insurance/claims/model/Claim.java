package com.insurance.claims.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "claims")
@SequenceGenerator(name = "claim_seq", sequenceName = "CLAIM_SEQ", allocationSize = 1)
public class Claim {

    public enum ClaimType { ACCIDENT, THEFT, FIRE, FLOOD, MEDICAL, DEATH, DISABILITY, TRAVEL_DELAY, OTHER }

    public enum ClaimStatus {
        DRAFT, SUBMITTED, UNDER_REVIEW, ASSESSMENT, PENDING_SETTLEMENT, SETTLED, REJECTED, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "claim_seq")
    @Column(name = "claim_id")
    private Long claimId;

    @Column(name = "claim_number", nullable = false, unique = true)
    private String claimNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "adjuster_id")
    private User assignedAdjuster;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false)
    private ClaimType claimType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status = ClaimStatus.DRAFT;

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    @Column(name = "incident_location", length = 500)
    private String incidentLocation;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "claimed_amount", precision = 15, scale = 2)
    private BigDecimal claimedAmount;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "claim", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ClaimDocument> documents;

    @JsonIgnore
    @OneToMany(mappedBy = "claim", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ClaimNote> notes;

    @JsonIgnore
    @OneToOne(mappedBy = "claim", fetch = FetchType.LAZY)
    private ClaimAssessment assessment;

    @JsonIgnore
    @OneToOne(mappedBy = "claim", fetch = FetchType.LAZY)
    private Settlement settlement;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }
    public User getClaimant() { return claimant; }
    public void setClaimant(User claimant) { this.claimant = claimant; }
    public User getAssignedAdjuster() { return assignedAdjuster; }
    public void setAssignedAdjuster(User assignedAdjuster) { this.assignedAdjuster = assignedAdjuster; }
    public ClaimType getClaimType() { return claimType; }
    public void setClaimType(ClaimType claimType) { this.claimType = claimType; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
    public String getIncidentLocation() { return incidentLocation; }
    public void setIncidentLocation(String incidentLocation) { this.incidentLocation = incidentLocation; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<ClaimDocument> getDocuments() { return documents; }
    public void setDocuments(List<ClaimDocument> documents) { this.documents = documents; }
    public List<ClaimNote> getNotes() { return notes; }
    public void setNotes(List<ClaimNote> notes) { this.notes = notes; }
    public ClaimAssessment getAssessment() { return assessment; }
    public void setAssessment(ClaimAssessment assessment) { this.assessment = assessment; }
    public Settlement getSettlement() { return settlement; }
    public void setSettlement(Settlement settlement) { this.settlement = settlement; }
}
