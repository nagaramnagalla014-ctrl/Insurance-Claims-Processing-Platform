package com.insurance.claims.controller;

import com.insurance.claims.dto.ApiResponse;
import com.insurance.claims.dto.AssessmentRequest;
import com.insurance.claims.model.Claim;
import com.insurance.claims.model.ClaimAssessment;
import com.insurance.claims.model.User;
import com.insurance.claims.service.AuthService;
import com.insurance.claims.service.ClaimAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class ClaimAssessmentController {

    @Autowired private ClaimAssessmentService assessmentService;
    @Autowired private AuthService authService;

    @GetMapping("/queue")
    public ResponseEntity<ApiResponse<List<Claim>>> getQueue() {
        return ResponseEntity.ok(ApiResponse.ok(assessmentService.getClaimsForAssessment()));
    }

    @PostMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<ClaimAssessment>> submit(
            @PathVariable Long claimId,
            @Valid @RequestBody AssessmentRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User adjuster = authService.getCurrentUser(userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.ok("Assessment submitted",
                assessmentService.submitAssessment(claimId, req, adjuster)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<ClaimAssessment>> getForClaim(@PathVariable Long claimId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(assessmentService.getAssessmentForClaim(claimId)));
        } catch (Exception e) {
            return ResponseEntity.notFound().<ApiResponse<ClaimAssessment>>build();
        }
    }
}
