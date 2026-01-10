package com.example.notesTogether.services;

import com.example.notesTogether.dto.note.NoteDto;
import com.example.notesTogether.dto.note.NotePayloadDto;
import com.example.notesTogether.entities.NoteVisibility;

import java.util.List;
import java.util.UUID;

public interface NoteService {
    List<NoteDto> fetchNotes(String actorEmail);
    NoteDto fetchNote(String actorEmail, UUID noteId);
    NotePayloadDto saveNote(NotePayloadDto note);
    NotePayloadDto updateNote(NotePayloadDto note);
    NotePayloadDto addUserToLiveUpdate(NotePayloadDto note);
    void deleteNote(String actorEmail, UUID noteId);
    void changeNoteVisibility(String userEmail, UUID noteId, NoteVisibility visibility);
}
