package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.NoteAccess;
import com.example.notesTogether.mappers.NoteAccessMapper;
import com.example.notesTogether.mappers.NoteMapper;

public class NoteAccessMapperImpl implements NoteAccessMapper {
    private final NoteMapper noteMapper;

    public NoteAccessMapperImpl(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    @Override
    public NoteAccess fromDto(NoteAccessDto noteAccessDto) {
        return new NoteAccess(
                noteAccessDto.id(),
                noteMapper.fromDto(noteAccessDto.note()),
                noteAccessDto.email(),
                noteAccessDto.role()
        );
    }

    @Override
    public NoteAccessDto toDto(NoteAccess noteAccess) {
        return new NoteAccessDto(
                noteAccess.getId(),
                noteMapper.toDto(noteAccess.getNote(), noteAccess.getRole()),
                noteAccess.getEmail(),
                noteAccess.getRole()
        );
    }
}
