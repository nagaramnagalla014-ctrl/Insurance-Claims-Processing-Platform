package com.insurance.claims.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_notes")
@SequenceGenerator(name = "note_seq", sequenceName = "CLAIM_NOTE_SEQ", allocationSize = 1)
public class ClaimNote {

    public enum NoteType { INTERNAL, CUSTOMER_VISIBLE, SYSTEM }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_seq")
    @Column(name = "note_id")
    private Long noteId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "note_type", nullable = false)
    private NoteType noteType = NoteType.INTERNAL;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }
    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public NoteType getNoteType() { return noteType; }
    public void setNoteType(NoteType noteType) { this.noteType = noteType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
