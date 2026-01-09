package com.example.notesTogether.services.impl;

import com.example.notesTogether.dto.noteAccess.AddNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.DeleteNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteAccess;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.repositories.NoteAccessRepository;
import com.example.notesTogether.repositories.NoteRepository;
import com.example.notesTogether.services.NoteAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class NoteAccessServiceImpl implements NoteAccessService {
    private final NoteRepository noteRepository;
    private final NoteAccessRepository noteAccessRepository;

    private static final Logger log =
            LoggerFactory.getLogger(NoteAccessServiceImpl.class);

    public NoteAccessServiceImpl(NoteRepository noteRepository, NoteAccessRepository noteAccessRepository) {
        this.noteRepository = noteRepository;
        this.noteAccessRepository = noteAccessRepository;
    }

    @Transactional
    @Override
    public NoteAccess addAccess(String userEmail, AddNoteAccessDto noteAccess) {
        Note note = validateOwnership(userEmail, noteAccess.noteId());

        List<NoteAccess> noteAccesses = note.getNoteAccesses() == null ? List.of() : note.getNoteAccesses();

        for (NoteAccess na : noteAccesses) {
            if (na.getEmail().equals(noteAccess.email())) {
                log.warn("Note access already exists for the email={}", noteAccess.email());
                throw new BadRequestException("Note access already exists for the provided email");
            }
        }
        return noteAccessRepository.save(
                new NoteAccess(
                        null,
                        note,
                        noteAccess.email(),
                        noteAccess.role()
                )
        );
    }

    @Transactional
    @Override
    public NoteAccess updateAccess(String userEmail, NoteAccessDto noteAccess) {
        validateOwnership(userEmail, noteAccess.noteId());
        NoteAccess updateNoteAccess = noteAccessRepository.findById(noteAccess.id())
                .orElseThrow(() -> {
                    log.warn("Note access not found id={}", noteAccess.id());
                    return new BadRequestException(
                            "Note access with this id is not registered."
                    );
                });
        updateNoteAccess.setRole(noteAccess.role());
        return noteAccessRepository.save(updateNoteAccess);
    }

    @Transactional
    @Override
    public void deleteAccess(String userEmail, DeleteNoteAccessDto noteAccess) {
        validateOwnership(userEmail, noteAccess.noteId());
        noteAccessRepository.deleteById(noteAccess.id());
    }

    private Note validateOwnership(String userEmail, UUID noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    log.warn("Note not found id={}", noteId);
                    return new BadRequestException(
                            "Note with this id does not exist."
                    );
                });

        if (note.getUser().getEmail().equals(userEmail)) return note;

        log.warn("User with the email={} is not the owner of this note", userEmail);
        throw new BadRequestException("User with the email is not the owner of this note");
    }
}
