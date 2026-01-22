package com.example.notesTogether.services;

import com.example.notesTogether.dto.noteVersion.NoteVersionDto;

import java.util.List;
import java.util.UUID;

public interface NoteVersionService {
    List<NoteVersionDto> fetchAllVersions(String actorEmail, UUID noteId);
    NoteVersionDto fetchVersion(String actorEmail, UUID noteId, UUID noteVersionId);
    NoteVersionDto restoreVersion(String actorEmail, UUID noteId, UUID noteVersionId);
}
