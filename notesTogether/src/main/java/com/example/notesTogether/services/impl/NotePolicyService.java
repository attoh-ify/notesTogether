package com.example.notesTogether.services.impl;

import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteAccess;
import com.example.notesTogether.entities.NoteAccessRole;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.repositories.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotePolicyService {
    private final NoteRepository noteRepository;

    private static final Logger log =
            LoggerFactory.getLogger(NotePolicyService.class);

    public NotePolicyService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note findNoteById(UUID noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    log.warn("Note not found id={}", noteId);
                    return new BadRequestException(
                            "Note with this id does not exist."
                    );
                });
    }

    public NoteAccessRole resolveRole(String actorEmail, Note note) {
        NoteAccessRole accessRole;

        accessRole = note.getUser().getEmail().equals(actorEmail) ? NoteAccessRole.OWNER : null;

        if (accessRole != null) return accessRole;

        for (NoteAccess noteAccess : note.getNoteAccesses()) {
            if (noteAccess.getEmail().equals(actorEmail)) {
                accessRole = noteAccess.getRole();
                break;
            }
        }
        return accessRole;
    }

    public Note isOwner(String userEmail, UUID noteId) {
        Note note = findNoteById(noteId);
        NoteAccessRole accessRole = resolveRole(userEmail, note);

        if (!accessRole.equals(NoteAccessRole.OWNER)) {
            log.warn("User with the email={} is not the owner of this note", userEmail);
            throw new BadRequestException("User with the email is not the owner of this note");
        }
        return note;
    }

    public Note isEditor(String userEmail, UUID noteId) {
        Note note = findNoteById(noteId);
        NoteAccessRole accessRole = resolveRole(userEmail, note);

        if (!accessRole.equals(NoteAccessRole.OWNER) && !accessRole.equals(NoteAccessRole.EDITOR)) {
            log.warn("User with the email={} is not allowed to edit this note", userEmail);
            throw new BadRequestException("User with the email is not allowed to edit this note");
        }
        return note;
    }
}
