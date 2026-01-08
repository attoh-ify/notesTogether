package com.example.notesTogether.services;

import com.example.notesTogether.dto.NoteDto;
import com.example.notesTogether.entities.Note;

import java.util.List;
import java.util.UUID;

public interface NoteService {
    Note createNote(UUID userId, Note note, String noteContent);
    List<NoteDto> fetchNotes(String userEmail);
    NoteDto saveNote(String userEmail, UUID noteId);
    NoteDto updateNote(String userEmail, UUID noteId);
    void deleteNote(UUID userId, UUID noteId);
}
