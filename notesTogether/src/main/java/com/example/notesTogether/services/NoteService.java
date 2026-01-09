package com.example.notesTogether.services;

import com.example.notesTogether.dto.NoteDto;
import com.example.notesTogether.dto.NotePayloadDto;

import java.util.List;
import java.util.UUID;

public interface NoteService {
    List<NoteDto> fetchNotes(String actorEmail);
    NotePayloadDto saveNote(String actorEmail, NotePayloadDto note);
    NotePayloadDto updateNote(String actorEmail, NotePayloadDto note);
    void deleteNote(String actorEmail, UUID noteId);
}
