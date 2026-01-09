package com.example.notesTogether.services.impl;

import com.example.notesTogether.dto.NoteDto;
import com.example.notesTogether.dto.NotePayloadDto;
import com.example.notesTogether.entities.*;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.repositories.NoteRepository;
import com.example.notesTogether.repositories.NoteVersionRepository;
import com.example.notesTogether.repositories.UserRepository;
import com.example.notesTogether.services.NoteService;
import com.example.notesTogether.utils.helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteMapper noteMapper;
    private final Cache noteCache;
    private final NoteVersionRepository noteVersionRepository;

    private static final Logger log =
            LoggerFactory.getLogger(NoteServiceImpl.class);

    public NoteServiceImpl(NoteRepository noteRepository, UserRepository userRepository, NoteMapper noteMapper, CacheManager cacheManager, NoteVersionRepository noteVersionRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.noteMapper = noteMapper;
        this.noteCache = cacheManager.getCache("NOTE_CACHE");
        this.noteVersionRepository = noteVersionRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<NoteDto> fetchNotes(String actorEmail) {
        List<Note> notes = noteRepository.findByActorEmail(actorEmail);
        List<NoteDto> noteDtos = new ArrayList<>();
        if (notes.isEmpty()) return List.of();
        for (Note note : notes) {
            NoteAccessRole accessRole = NoteAccessRole.OWNER;
            if (!note.getUser().getEmail().equals(actorEmail)) {
                for (NoteAccess noteAccess: note.getNoteAccesses()) {
                    if (noteAccess.getEmail().equals(actorEmail)) {
                        accessRole = noteAccess.getRole();
                    }
                }
            }
            NoteDto noteDto = noteMapper.toDto(note, accessRole);
            noteDtos.add(noteDto);
        }
        return noteDtos;
    }

    @Transactional
    @Override
    public NotePayloadDto saveNote(String actorEmail, NotePayloadDto note) {
        User user = userRepository.findByEmail(actorEmail)
                .orElseThrow(() -> {
                    log.warn("User not found email={}", actorEmail);
                    return new BadRequestException(
                            "User with this email is not registered."
                    );
                });

        if (helpers.isBlank(note.title()))
            throw new BadRequestException("Note title is required");

        int versionNumber;

        if (note.id() == null) {
            versionNumber = 1;
            Note newNote = noteRepository.save(
                    new Note(
                            null,
                            user,
                            NoteVisibility.PUBLIC,
                            null,
                            null,
                            null
                    )
            );

            NoteVersion firstNoteVersion = noteVersionRepository.save(
                    new NoteVersion(
                            null,
                            newNote,
                            note.title(),
                            note.content(),
                            user.getId(),
                            versionNumber
                    )
            );

            newNote.setCurrentNoteVersion(firstNoteVersion.getId());
            newNote.setNoteVersions(List.of(firstNoteVersion));
        } else {
            Note updateNote = validateActorEditingPermission(actorEmail, note.id());

            versionNumber = Optional.ofNullable(noteVersionRepository.findMaxVersionByNoteId(note.id()))
                    .map(v -> v + 1)
                    .orElse(1);

            NoteVersion newNoteVersion = noteVersionRepository.save(
                    new NoteVersion(
                            null,
                            updateNote,
                            note.title(),
                            note.content(),
                            user.getId(),
                            versionNumber
                    )
            );

            updateNote.setCurrentNoteVersion(newNoteVersion.getId());
            updateNote.getNoteVersions().add(newNoteVersion);
            noteRepository.save(updateNote);

            if (noteCache != null) {
                noteCache.evict(note.id());
            }
        }
        return note;
    }

    @Transactional
    @Override
    public NotePayloadDto updateNote(String actorEmail, NotePayloadDto note) {
        validateActorEditingPermission(actorEmail, note.id());

        if (noteCache != null) {
            noteCache.put(note.id(), note);
        }
        return note;
    }

    @Transactional
    @Override
    public void deleteNote(String actorEmail, UUID noteId) {
        Note note = validateActorEditingPermission(actorEmail, noteId);
        noteRepository.delete(note);

        if (noteCache != null) {
            noteCache.evict(noteId);
        }
    }

    private Note validateActorEditingPermission(String actorEmail, UUID noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    log.warn("Note not found id={}", noteId);
                    return new BadRequestException(
                            "Note with this id does not exist."
                    );
                });

        List<NoteAccess> noteAccesses = note.getNoteAccesses();

        boolean isOwner = note.getUser().getEmail().equals(actorEmail);

        boolean isEditor = false;
        for (NoteAccess noteAccess: noteAccesses) {
            if (noteAccess.getEmail().equals(actorEmail) && noteAccess.getRole().equals(NoteAccessRole.EDITOR)) {
                isEditor = true;
                break;
            }
        }

        if (isOwner || isEditor) return note;

        log.warn("User with the email={} is not allowed to edit this note", actorEmail);
        throw new BadRequestException("User with the email is not allowed to edit this note");
    }
}
