package com.insurance.claims.controller;

import com.insurance.claims.dto.ApiResponse;
import com.insurance.claims.model.ClaimDocument;
import com.insurance.claims.model.User;
import com.insurance.claims.service.AuthService;
import com.insurance.claims.service.ClaimDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class ClaimDocumentController {

    @Autowired private ClaimDocumentService documentService;
    @Autowired private AuthService authService;

    @PostMapping("/upload/{claimId}")
    public ResponseEntity<ApiResponse<ClaimDocument>> upload(
            @PathVariable Long claimId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authService.getCurrentUser(userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.ok("Document uploaded",
                documentService.uploadDocument(claimId, file, documentType, user)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<List<ClaimDocument>>> getForClaim(@PathVariable Long claimId) {
        return ResponseEntity.ok(ApiResponse.ok(documentService.getDocumentsForClaim(claimId)));
    }

    @PutMapping("/{documentId}/verify")
    public ResponseEntity<ApiResponse<ClaimDocument>> verify(
            @PathVariable Long documentId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String remarks = body.get("remarks");
        return ResponseEntity.ok(ApiResponse.ok("Document verified",
            documentService.verifyDocument(documentId, status, remarks)));
    }
}
