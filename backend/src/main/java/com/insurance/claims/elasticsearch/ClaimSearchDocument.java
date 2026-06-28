package com.insurance.claims.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(indexName = "claims")
public class ClaimSearchDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String claimNumber;

    @Field(type = FieldType.Keyword)
    private String policyNumber;

    @Field(type = FieldType.Keyword)
    private String claimType;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String incidentLocation;

    @Field(type = FieldType.Keyword)
    private String policyholderName;

    @Field(type = FieldType.Keyword)
    private String policyholderEmail;

    @Field(type = FieldType.Text)
    private String adjusterName;

    @Field(type = FieldType.Double)
    private BigDecimal claimedAmount;

    @Field(type = FieldType.Date)
    private LocalDateTime submittedAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIncidentLocation() { return incidentLocation; }
    public void setIncidentLocation(String incidentLocation) { this.incidentLocation = incidentLocation; }
    public String getPolicyholderName() { return policyholderName; }
    public void setPolicyholderName(String policyholderName) { this.policyholderName = policyholderName; }
    public String getPolicyholderEmail() { return policyholderEmail; }
    public void setPolicyholderEmail(String policyholderEmail) { this.policyholderEmail = policyholderEmail; }
    public String getAdjusterName() { return adjusterName; }
    public void setAdjusterName(String adjusterName) { this.adjusterName = adjusterName; }
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
