package com.insurance.claims.service;

import com.insurance.claims.elasticsearch.ClaimSearchDocument;
import com.insurance.claims.elasticsearch.ClaimSearchRepository;
import com.insurance.claims.model.Claim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.elasticsearch.index.query.QueryBuilders;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ClaimSearchService.class);

    @Autowired private ClaimSearchRepository searchRepository;
    @Autowired private ElasticsearchOperations elasticsearchOperations;

    @Async
    public void indexClaim(Claim claim) {
        try {
            ClaimSearchDocument doc = toDocument(claim);
            searchRepository.save(doc);
        } catch (Exception e) {
            logger.warn("Failed to index claim {}: {}", claim.getClaimNumber(), e.getMessage());
        }
    }

    public List<ClaimSearchDocument> search(String keyword) {
        var query = new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.multiMatchQuery(keyword,
                "claimNumber", "description", "policyholderName", "incidentLocation", "policyNumber"))
            .build();
        return elasticsearchOperations.search(query, ClaimSearchDocument.class)
            .getSearchHits().stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
    }

    public List<ClaimSearchDocument> searchByStatus(String status) {
        return searchRepository.findByStatus(status);
    }

    public List<ClaimSearchDocument> searchByEmail(String email) {
        return searchRepository.findByPolicyholderEmail(email);
    }

    private ClaimSearchDocument toDocument(Claim claim) {
        ClaimSearchDocument doc = new ClaimSearchDocument();
        doc.setId(claim.getClaimId().toString());
        doc.setClaimNumber(claim.getClaimNumber());
        doc.setPolicyNumber(claim.getPolicy().getPolicyNumber());
        doc.setClaimType(claim.getClaimType().name());
        doc.setStatus(claim.getStatus().name());
        doc.setDescription(claim.getDescription());
        doc.setIncidentLocation(claim.getIncidentLocation());
        doc.setClaimedAmount(claim.getClaimedAmount());
        doc.setPolicyholderName(claim.getClaimant().getFullName());
        doc.setPolicyholderEmail(claim.getClaimant().getEmail());
        if (claim.getAssignedAdjuster() != null) {
            doc.setAdjusterName(claim.getAssignedAdjuster().getFullName());
        }
        doc.setSubmittedAt(claim.getSubmittedAt());
        doc.setUpdatedAt(claim.getUpdatedAt());
        return doc;
    }
}
