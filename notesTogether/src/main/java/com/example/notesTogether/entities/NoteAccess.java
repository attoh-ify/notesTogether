package com.example.notesTogether.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "note_access",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"note_id", "email"})
        }
)
public class NoteAccess {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private NoteAccessRole role;

    public NoteAccess() {}

    public NoteAccess(UUID id, Note note, String email, NoteAccessRole role) {
        this.id = id;
        this.note = note;
        this.email = email;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public NoteAccessRole getRole() {
        return role;
    }

    public void setRole(NoteAccessRole role) {
        this.role = role;
    }
}
