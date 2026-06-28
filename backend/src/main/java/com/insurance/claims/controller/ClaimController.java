package com.insurance.claims.controller;

import com.insurance.claims.dto.ApiResponse;
import com.insurance.claims.dto.ClaimRequest;
import com.insurance.claims.elasticsearch.ClaimSearchDocument;
import com.insurance.claims.model.Claim;
import com.insurance.claims.model.User;
import com.insurance.claims.service.AuthService;
import com.insurance.claims.service.ClaimSearchService;
import com.insurance.claims.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired private ClaimService claimService;
    @Autowired private ClaimSearchService searchService;
    @Autowired private AuthService authService;

    @PostMapping
    public ResponseEntity<ApiResponse<Claim>> submit(
            @Valid @RequestBody ClaimRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Claim submitted", claimService.submitClaim(req, user)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Claim>>> myClaims(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(claimService.getClaimsForUser(user)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Claim>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(claimService.getById(id)));
    }

    @GetMapping("/track/{claimNumber}")
    public ResponseEntity<ApiResponse<Claim>> track(@PathVariable String claimNumber) {
        return ResponseEntity.ok(ApiResponse.ok(claimService.getByClaimNumber(claimNumber)));
    }

    @GetMapping("/open")
    public ResponseEntity<ApiResponse<List<Claim>>> getOpen() {
        return ResponseEntity.ok(ApiResponse.ok(claimService.getOpenClaims()));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<Claim>> assignAdjuster(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User adjuster = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Claim assigned", claimService.assignAdjuster(id, adjuster)));
    }

    @GetMapping("/adjuster/my")
    public ResponseEntity<ApiResponse<List<Claim>>> myAssignedClaims(
            @AuthenticationPrincipal UserDetails userDetails) {
        User adjuster = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(claimService.getClaimsForAdjuster(adjuster)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ClaimSearchDocument>>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok(searchService.search(q)));
    }
}
