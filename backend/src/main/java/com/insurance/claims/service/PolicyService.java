package com.insurance.claims.service;

import com.insurance.claims.exception.ClaimException;
import com.insurance.claims.model.Policy;
import com.insurance.claims.model.User;
import com.insurance.claims.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PolicyService {

    @Autowired private PolicyRepository policyRepository;

    public List<Policy> getPoliciesForUser(User user) {
        return policyRepository.findByPolicyholder(user);
    }

    public Policy getById(Long id) {
        return policyRepository.findById(id)
            .orElseThrow(() -> new ClaimException("Policy not found: " + id));
    }

    public Policy getByPolicyNumber(String number) {
        return policyRepository.findByPolicyNumber(number)
            .orElseThrow(() -> new ClaimException("Policy not found: " + number));
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }
}
