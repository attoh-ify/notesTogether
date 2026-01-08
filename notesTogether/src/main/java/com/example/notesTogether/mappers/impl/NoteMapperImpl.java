package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.NoteDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteAccessRole;
import com.example.notesTogether.mappers.NoteAccessMapper;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.mappers.NoteVersionMapper;
import com.example.notesTogether.mappers.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NoteMapperImpl implements NoteMapper {
    private final NoteVersionMapper noteVersionMapper;
    private final NoteAccessMapper noteAccessMapper;
    private final UserMapper userMapper;

    public NoteMapperImpl(NoteVersionMapper noteVersionMapper, NoteAccessMapper noteAccessMapper, UserMapper userMapper) {
        this.noteVersionMapper = noteVersionMapper;
        this.noteAccessMapper = noteAccessMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Note fromDto(NoteDto noteDto) {
        return new Note(
                noteDto.id(),
                noteDto.title(),
                userMapper.fromDto(noteDto.user()),
                noteDto.visibility(),
                Optional.ofNullable(noteDto.noteAccesses())
                        .map(notes -> notes.stream()
                                .map(noteAccessMapper::fromDto)
                                .toList()
                        ).orElse(null),
                noteDto.currentNoteVersion(),
                Optional.ofNullable(noteDto.noteVersions())
                        .map(notes -> notes.stream()
                                .map(noteVersionMapper::fromDto)
                                .toList()
                        ).orElse(null)
        );
    }

    @Override
    public NoteDto toDto(Note note, NoteAccessRole accessRole) {
        return new NoteDto(
                note.getId(),
                note.getTitle(),
                userMapper.toDto(note.getUser()),
                note.getVisibility(),
                Optional.ofNullable(note.getNoteAccesses())
                        .map(notes -> notes.stream()
                                .map(noteAccessMapper::toDto)
                                .toList()
                        ).orElse(null),
                accessRole,
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
