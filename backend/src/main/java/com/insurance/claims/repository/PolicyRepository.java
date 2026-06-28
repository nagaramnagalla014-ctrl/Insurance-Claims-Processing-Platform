package com.insurance.claims.repository;

import com.insurance.claims.model.Policy;
import com.insurance.claims.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findByPolicyNumber(String policyNumber);
    List<Policy> findByPolicyholder(User policyholder);
    List<Policy> findByStatus(Policy.PolicyStatus status);
    boolean existsByPolicyNumber(String policyNumber);
}
