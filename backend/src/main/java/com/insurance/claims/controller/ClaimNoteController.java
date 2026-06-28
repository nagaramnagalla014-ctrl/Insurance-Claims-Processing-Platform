package com.insurance.claims.controller;

import com.insurance.claims.dto.ApiResponse;
import com.insurance.claims.dto.ClaimNoteRequest;
import com.insurance.claims.model.ClaimNote;
import com.insurance.claims.model.User;
import com.insurance.claims.service.AuthService;
import com.insurance.claims.service.ClaimNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class ClaimNoteController {

    @Autowired private ClaimNoteService noteService;
    @Autowired private AuthService authService;

    @PostMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<ClaimNote>> addNote(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimNoteRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Note added", noteService.addNote(claimId, req, user)));
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<List<ClaimNote>>> getNotes(
            @PathVariable Long claimId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(noteService.getNotesForClaim(claimId, user)));
    }
}
