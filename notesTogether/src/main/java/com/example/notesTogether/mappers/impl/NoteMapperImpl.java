package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.note.NoteDto;
import com.example.notesTogether.dto.noteVersion.NoteVersionDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteAccessRole;
import com.example.notesTogether.entities.NoteVersion;
import com.example.notesTogether.mappers.NoteAccessMapper;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.mappers.NoteVersionMapper;
import com.example.notesTogether.mappers.UserMapper;
import com.example.notesTogether.repositories.NoteVersionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class NoteMapperImpl implements NoteMapper {
    private final NoteVersionMapper noteVersionMapper;
    private final NoteAccessMapper noteAccessMapper;
    private final UserMapper userMapper;
    private final NoteVersionRepository noteVersionRepository;

    public NoteMapperImpl(NoteVersionMapper noteVersionMapper, NoteAccessMapper noteAccessMapper, UserMapper userMapper, NoteVersionRepository noteVersionRepository) {
        this.noteVersionMapper = noteVersionMapper;
        this.noteAccessMapper = noteAccessMapper;
        this.userMapper = userMapper;
        this.noteVersionRepository = noteVersionRepository;
    }

    @Override
    public Note fromDto(NoteDto noteDto) {
        return new Note(
                noteDto.id(),
                userMapper.fromDto(noteDto.user()),
                noteDto.visibility(),
                Optional.ofNullable(noteDto.noteAccesses())
                        .map(noteAccesses -> noteAccesses.stream()
                                .map(noteAccessMapper::fromDto)
                                .toList()
                        ).orElse(null),
                noteDto.currentNoteVersion(),
                Optional.ofNullable(noteDto.noteVersions())
                        .map(noteVersions -> noteVersions.stream()
                                .map(noteVersionMapper::fromDto)
                                .toList()
                        ).orElse(null)
        );
    }

    @Override
    public NoteDto toDto(Note note, NoteAccessRole accessRole) {
        return new NoteDto(
                note.getId(),
                userMapper.toDto(note.getUser()),
                note.getVisibility(),
                Optional.ofNullable(note.getNoteAccesses())
                        .map(noteAccesses -> noteAccesses.stream()
                                .filter(ar -> accessRole == NoteAccessRole.OWNER)
                                .map(noteAccessMapper::toDto)
                                .toList()
                        ).orElse(null),
                accessRole,
                note.getCurrentNoteVersion(),
                List.of(getCurrentNoteVersion(note.getCurrentNoteVersion())),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }

    private NoteVersionDto getCurrentNoteVersion(UUID currentNoteVersionId) {
        Optional<NoteVersion> noteVersion = noteVersionRepository.findById(currentNoteVersionId);
        return noteVersion.map(noteVersionMapper::toDto).orElse(null);
    }
}
