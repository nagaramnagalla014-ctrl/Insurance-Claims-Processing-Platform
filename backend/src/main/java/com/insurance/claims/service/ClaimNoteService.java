package com.insurance.claims.service;

import com.insurance.claims.dto.ClaimNoteRequest;
import com.insurance.claims.model.Claim;
import com.insurance.claims.model.ClaimNote;
import com.insurance.claims.model.User;
import com.insurance.claims.repository.ClaimNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimNoteService {

    @Autowired private ClaimNoteRepository noteRepository;
    @Autowired private ClaimService claimService;

    public ClaimNote addNote(Long claimId, ClaimNoteRequest req, User author) {
        Claim claim = claimService.getById(claimId);
        ClaimNote note = new ClaimNote();
        note.setClaim(claim);
        note.setAuthor(author);
        note.setContent(req.getContent());
        note.setNoteType(ClaimNote.NoteType.valueOf(req.getNoteType()));
        return noteRepository.save(note);
    }

    public List<ClaimNote> getNotesForClaim(Long claimId, User viewer) {
        Claim claim = claimService.getById(claimId);
        List<ClaimNote> all = noteRepository.findByClaimOrderByCreatedAtDesc(claim);
        if (viewer.getRole() == com.insurance.claims.model.User.Role.POLICYHOLDER) {
            return all.stream()
                .filter(n -> n.getNoteType() == ClaimNote.NoteType.CUSTOMER_VISIBLE
                          || n.getNoteType() == ClaimNote.NoteType.SYSTEM)
                .collect(Collectors.toList());
        }
        return all;
    }
}
