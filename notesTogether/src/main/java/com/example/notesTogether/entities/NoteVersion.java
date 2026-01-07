package com.example.notesTogether.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "note_versions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"note_id", "version_number"})
        }
)
public class NoteVersion {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "note_id", nullable = false)
    private UUID noteId;

    @Lob
    @Column(name = "content_json", nullable = false)
    private String contentJson;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public NoteVersion() {}

    public NoteVersion(
            UUID id,
            UUID noteId,
            String contentJson,
            UUID createdBy,
            Integer versionNumber
    ) {
        this.id = id;
        this.noteId = noteId;
        this.contentJson = contentJson;
        this.createdBy = createdBy;
        this.versionNumber = versionNumber;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getNoteId() {
        return noteId;
    }

    public void setNoteId(UUID noteId) {
        this.noteId = noteId;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}