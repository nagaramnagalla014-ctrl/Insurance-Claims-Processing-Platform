package com.insurance.claims.controller;

import com.insurance.claims.dto.ApiResponse;
import com.insurance.claims.model.Policy;
import com.insurance.claims.model.User;
import com.insurance.claims.service.AuthService;
import com.insurance.claims.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired private PolicyService policyService;
    @Autowired private AuthService authService;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Policy>>> myPolicies(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(policyService.getPoliciesForUser(user)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Policy>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(policyService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Policy>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(policyService.getAllPolicies()));
    }
}
