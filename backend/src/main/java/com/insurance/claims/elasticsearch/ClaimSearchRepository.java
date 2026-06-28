package com.insurance.claims.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface ClaimSearchRepository extends ElasticsearchRepository<ClaimSearchDocument, String> {

    List<ClaimSearchDocument> findByStatus(String status);

    List<ClaimSearchDocument> findByPolicyholderEmail(String email);

    List<ClaimSearchDocument> findByClaimNumberContaining(String claimNumber);

    List<ClaimSearchDocument> findByPolicyNumber(String policyNumber);
}
