package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.NoteDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.mappers.NoteVersionMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NoteMapperImpl implements NoteMapper {
    private final NoteVersionMapper noteVersionMapper;

    public NoteMapperImpl(NoteVersionMapper noteVersionMapper) {
        this.noteVersionMapper = noteVersionMapper;
    }

    @Override
    public Note fromDto(NoteDto noteDto) {
        return new Note(
                noteDto.id(),
                noteDto.title(),
                noteDto.user(),
                noteDto.visibility(),
                noteDto.collaborators(),
                noteDto.viewers(),
                noteDto.currentNoteVersion(),
                Optional.ofNullable(noteDto.noteVersions())
                        .map(notes -> notes.stream()
                                .map(noteVersionMapper::fromDto)
                                .toList()
                        ).orElse(null)
        );
    }

    @Override
    public NoteDto toDto(Note note) {
        return new NoteDto(
                note.getId(),
                note.getTitle(),
                note.getUser(),
                note.getVisibility(),
                note.getCollaborators(),
                note.getViewers(),
                note.getCurrentNoteVersion(),
                Optional.ofNullable(note.getNoteVersions())
                                .map(notes -> notes.stream()
                                        .map(noteVersionMapper::toDto)
                                        .toList()
                                ).orElse(null),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
