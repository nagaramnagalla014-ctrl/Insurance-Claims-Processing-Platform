package com.insurance.claims.repository;

import com.insurance.claims.model.Claim;
import com.insurance.claims.model.ClaimNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimNoteRepository extends JpaRepository<ClaimNote, Long> {
    List<ClaimNote> findByClaimOrderByCreatedAtDesc(Claim claim);
    List<ClaimNote> findByClaimAndNoteType(Claim claim, ClaimNote.NoteType noteType);
}
