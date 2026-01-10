package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.note.NoteDto;
import com.example.notesTogether.dto.noteVersion.NoteVersionDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteAccessRole;
import com.example.notesTogether.entities.NoteVersion;
import com.example.notesTogether.entities.User;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.mappers.NoteAccessMapper;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.mappers.NoteVersionMapper;
import com.example.notesTogether.repositories.NoteVersionRepository;
import com.example.notesTogether.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class NoteMapperImpl implements NoteMapper {
    private final NoteVersionMapper noteVersionMapper;
    private final NoteAccessMapper noteAccessMapper;
    private final NoteVersionRepository noteVersionRepository;
    private final UserRepository userRepository;

    private static final Logger log =
            LoggerFactory.getLogger(NoteMapperImpl.class);

    public NoteMapperImpl(NoteVersionMapper noteVersionMapper, NoteAccessMapper noteAccessMapper, NoteVersionRepository noteVersionRepository, UserRepository userRepository) {
        this.noteVersionMapper = noteVersionMapper;
        this.noteAccessMapper = noteAccessMapper;
        this.noteVersionRepository = noteVersionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Note fromDto(NoteDto noteDto) {
        User user = userRepository.findById(noteDto.userId())
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", noteDto.userId());
                    return new BadRequestException("User with id not found");
                });
        return new Note(
                noteDto.id(),
                user,
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
                note.getUser().getId(),
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
