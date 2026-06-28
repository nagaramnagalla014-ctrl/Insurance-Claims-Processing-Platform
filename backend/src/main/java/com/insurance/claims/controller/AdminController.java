package com.insurance.claims.controller;

import com.insurance.claims.dto.ApiResponse;
import com.insurance.claims.model.Claim;
import com.insurance.claims.model.Policy;
import com.insurance.claims.model.User;
import com.insurance.claims.repository.UserRepository;
import com.insurance.claims.service.ClaimService;
import com.insurance.claims.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private ClaimService claimService;
    @Autowired private PolicyService policyService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard() {
        Map<String, Object> stats = new HashMap<>();
        List<Claim> all = claimService.getAllClaims();
        stats.put("totalClaims", all.size());
        stats.put("totalPolicies", policyService.getAllPolicies().size());
        stats.put("totalUsers", userRepository.count());
        stats.put("submitted", claimService.getClaimsByStatus(Claim.ClaimStatus.SUBMITTED).size());
        stats.put("underReview", claimService.getClaimsByStatus(Claim.ClaimStatus.UNDER_REVIEW).size());
        stats.put("pendingSettlement", claimService.getClaimsByStatus(Claim.ClaimStatus.PENDING_SETTLEMENT).size());
        stats.put("settled", claimService.getClaimsByStatus(Claim.ClaimStatus.SETTLED).size());
        stats.put("rejected", claimService.getClaimsByStatus(Claim.ClaimStatus.REJECTED).size());
        stats.put("recentClaims", all.stream().limit(10).toArray());
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/claims")
    public ResponseEntity<ApiResponse<List<Claim>>> getAllClaims(
            @RequestParam(required = false) String status) {
        List<Claim> claims = (status != null && !status.trim().isEmpty())
            ? claimService.getClaimsByStatus(Claim.ClaimStatus.valueOf(status))
            : claimService.getAllClaims();
        return ResponseEntity.ok(ApiResponse.ok(claims));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
            @RequestParam(required = false) String role) {
        List<User> users = (role != null && !role.trim().isEmpty())
            ? userRepository.findByRole(User.Role.valueOf(role))
            : userRepository.findAll();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PutMapping("/users/{userId}/toggle")
    public ResponseEntity<ApiResponse<User>> toggleUser(@PathVariable Long userId) {
        return userRepository.findById(userId).map(user -> {
            user.setIsActive(!user.getIsActive());
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.ok("Status toggled", user));
        }).orElse(ResponseEntity.notFound().<ApiResponse<User>>build());
    }

    @GetMapping("/policies")
    public ResponseEntity<ApiResponse<List<Policy>>> getAllPolicies() {
        return ResponseEntity.ok(ApiResponse.ok(policyService.getAllPolicies()));
    }
}
