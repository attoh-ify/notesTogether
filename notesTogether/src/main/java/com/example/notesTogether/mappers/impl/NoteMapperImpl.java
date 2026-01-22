package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.note.NoteDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteAccessRole;
import com.example.notesTogether.entities.User;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.repositories.UserRepository;
import com.example.notesTogether.services.impl.NotePolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NoteMapperImpl implements NoteMapper {
    private final UserRepository userRepository;
    private final NotePolicyService notePolicyService;

    private static final Logger log =
            LoggerFactory.getLogger(NoteMapperImpl.class);

    public NoteMapperImpl(UserRepository userRepository, NotePolicyService notePolicyService) {
        this.userRepository = userRepository;
        this.notePolicyService = notePolicyService;
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
                noteDto.title(),
                noteDto.visibility(),
                null,
                noteDto.currentNoteVersion(),
                null
        );
    }

    @Override
    public NoteDto toDto(Note note) {
        NoteAccessRole accessRole = notePolicyService.resolveRole(note.getUser().getEmail(), note);
        return new NoteDto(
                note.getId(),
                note.getUser().getId(),
                note.getTitle(),
                note.getVisibility(),
                accessRole,
                note.getCurrentNoteVersion(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
