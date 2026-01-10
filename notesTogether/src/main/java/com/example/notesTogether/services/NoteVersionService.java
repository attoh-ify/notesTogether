package com.example.notesTogether.services;

import com.example.notesTogether.entities.NoteVersion;

import java.util.List;
import java.util.UUID;

public interface NoteVersionService {
    List<NoteVersion> fetchAllVersions(String actorEmail, UUID noteId);
    NoteVersion fetchVersion(String actorEmail, UUID noteId, UUID noteVersionId);
    NoteVersion restoreVersion(String actorEmail, UUID noteId, UUID noteVersionId);
}
