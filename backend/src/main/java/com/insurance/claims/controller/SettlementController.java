package com.insurance.claims.controller;

import com.insurance.claims.dto.ApiResponse;
import com.insurance.claims.dto.SettlementRequest;
import com.insurance.claims.model.Claim;
import com.insurance.claims.model.Settlement;
import com.insurance.claims.model.User;
import com.insurance.claims.service.AuthService;
import com.insurance.claims.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    @Autowired private SettlementService settlementService;
    @Autowired private AuthService authService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Claim>>> getPending() {
        return ResponseEntity.ok(ApiResponse.ok(settlementService.getPendingSettlements()));
    }

    @PostMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<Settlement>> settle(
            @PathVariable Long claimId,
            @Valid @RequestBody SettlementRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User processor = authService.getCurrentUser(userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.ok("Settlement processed",
                settlementService.processSettlement(claimId, req, processor)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/claim/{claimId}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long claimId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User processor = authService.getCurrentUser(userDetails.getUsername());
            settlementService.rejectClaim(claimId, body.get("reason"), processor);
            return ResponseEntity.ok(ApiResponse.ok("Claim rejected", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<Settlement>> getForClaim(@PathVariable Long claimId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(settlementService.getSettlementForClaim(claimId)));
        } catch (Exception e) {
            return ResponseEntity.notFound().<ApiResponse<Settlement>>build();
        }
    }
}
