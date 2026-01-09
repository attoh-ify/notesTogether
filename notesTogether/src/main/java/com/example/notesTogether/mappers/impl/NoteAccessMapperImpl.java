package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.NoteAccess;
import com.example.notesTogether.mappers.NoteAccessMapper;

public class NoteAccessMapperImpl implements NoteAccessMapper {
    @Override
    public NoteAccess fromDto(NoteAccessDto noteAccessDto) {
        return new NoteAccess(
                noteAccessDto.id(),
                null,
                noteAccessDto.email(),
                noteAccessDto.role()
        );
    }

    @Override
    public NoteAccessDto toDto(NoteAccess noteAccess) {
        return new NoteAccessDto(
                noteAccess.getId(),
                noteAccess.getNote().getId(),
                noteAccess.getEmail(),
                noteAccess.getRole()
        );
    }
}
