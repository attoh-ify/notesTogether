package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteAccess;
import com.example.notesTogether.mappers.NoteAccessMapper;
import com.example.notesTogether.services.impl.NotePolicyService;
import org.springframework.stereotype.Component;

@Component
public class NoteAccessMapperImpl implements NoteAccessMapper {
    private final NotePolicyService notePolicyService;

    public NoteAccessMapperImpl(NotePolicyService notePolicyService) {
        this.notePolicyService = notePolicyService;
    }

    @Override
    public NoteAccess fromDto(NoteAccessDto noteAccessDto) {
        Note note = noteAccessDto.noteId() != null ? notePolicyService.findNoteById(noteAccessDto.noteId()) : null;
        return new NoteAccess(
                noteAccessDto.id(),
                note,
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
