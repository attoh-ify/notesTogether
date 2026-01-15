package com.example.notesTogether.mappers;

import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.NoteAccess;

import java.util.UUID;

public interface NoteAccessMapper {
    NoteAccess fromDto(NoteAccessDto noteAccessDto, UUID noteId);
    NoteAccessDto toDto(NoteAccess noteAccess);
}
