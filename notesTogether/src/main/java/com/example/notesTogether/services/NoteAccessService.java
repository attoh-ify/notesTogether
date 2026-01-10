package com.example.notesTogether.services;

import com.example.notesTogether.dto.noteAccess.AddNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.DeleteNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.NoteAccessDto;

import java.util.List;
import java.util.UUID;

public interface NoteAccessService {
    NoteAccessDto addAccess(String userEmail, AddNoteAccessDto noteAccess);
    NoteAccessDto updateAccess(String userEmail, NoteAccessDto noteAccess);
    void  deleteAccess(String userEmail, DeleteNoteAccessDto noteAccess);
    List<NoteAccessDto> getAllAccess(String userEmail, UUID noteId);
}
