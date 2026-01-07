package com.example.notesTogether.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "note_access",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"note_id", "user_id"})
        }
)
public class NoteAccess {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "note_id", nullable = false)
    private UUID noteId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private NoteAccessRole role;

    public NoteAccess() {}

    public NoteAccess(UUID id, UUID noteId, UUID userId, NoteAccessRole role) {
        this.id = id;
        this.noteId = noteId;
        this.userId = userId;
        this.role = role;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public NoteAccessRole getRole() {
        return role;
    }

    public void setRole(NoteAccessRole role) {
        this.role = role;
    }
}