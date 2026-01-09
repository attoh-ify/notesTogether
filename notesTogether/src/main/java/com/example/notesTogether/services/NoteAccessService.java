package com.example.notesTogether.services;

import com.example.notesTogether.dto.noteAccess.AddNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.DeleteNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.NoteAccess;

public interface NoteAccessService {
    NoteAccess addAccess(String userEmail, AddNoteAccessDto noteAccess);
    NoteAccess updateAccess(String userEmail, NoteAccessDto noteAccess);
    void  deleteAccess(String userEmail, DeleteNoteAccessDto noteAccess);
}
