package com.example.notesTogether.mappers;

import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.NoteAccess;

public interface NoteAccessMapper {
    NoteAccess fromDto(NoteAccessDto noteAccessDto);
    NoteAccessDto toDto(NoteAccess noteAccess);
}
