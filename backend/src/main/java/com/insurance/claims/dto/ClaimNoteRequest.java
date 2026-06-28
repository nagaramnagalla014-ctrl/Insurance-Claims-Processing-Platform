package com.insurance.claims.dto;

import javax.validation.constraints.NotBlank;

public class ClaimNoteRequest {
    @NotBlank private String content;
    private String noteType = "INTERNAL";

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getNoteType() { return noteType; }
    public void setNoteType(String noteType) { this.noteType = noteType; }
}
