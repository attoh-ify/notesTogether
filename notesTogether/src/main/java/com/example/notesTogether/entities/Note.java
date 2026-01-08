package com.example.notesTogether.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private NoteVisibility visibility;

    @OneToMany(mappedBy = "note", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<NoteAccess> noteAccesses;

    @Column(name = "current_note_version")
    private UUID currentNoteVersion;

    @OneToMany(mappedBy = "note", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<NoteVersion> noteVersions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Note() {}

    public Note(UUID id, String title, User user, NoteVisibility visibility,  List<NoteAccess> noteAccesses, UUID currentNoteVersion, List<NoteVersion> noteVersions) {
        this.id = id;
        this.title = title;
        this.user = user;
        this.visibility = visibility;
        this.noteAccesses = noteAccesses;
        this.currentNoteVersion = currentNoteVersion;
        this.noteVersions = noteVersions;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NoteVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(NoteVisibility visibility) {
        this.visibility = visibility;
    }

    public List<NoteAccess> getNoteAccesses() {
        return noteAccesses;
    }

    public void setNoteAccesses(List<NoteAccess> noteAccesses) {
        this.noteAccesses = noteAccesses;
    }

    public UUID getCurrentNoteVersion() {
        return currentNoteVersion;
    }

    public void setCurrentNoteVersion(UUID currentNoteVersion) {
        this.currentNoteVersion = currentNoteVersion;
    }

    public List<NoteVersion> getNoteVersions() {
        return noteVersions;
    }

    public void setNoteVersions(List<NoteVersion> noteVersions) {
        this.noteVersions = noteVersions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}