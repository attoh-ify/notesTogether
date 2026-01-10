package com.example.notesTogether.services.impl;

import com.example.notesTogether.dto.note.NoteDto;
import com.example.notesTogether.dto.note.NotePayloadDto;
import com.example.notesTogether.entities.*;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.repositories.NoteRepository;
import com.example.notesTogether.repositories.NoteVersionRepository;
import com.example.notesTogether.repositories.UserRepository;
import com.example.notesTogether.services.NoteService;
import com.example.notesTogether.utils.Helpers;
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
    private final NotePolicyService notePolicyService;

    private static final Logger log =
            LoggerFactory.getLogger(NoteServiceImpl.class);

    public NoteServiceImpl(NoteRepository noteRepository, UserRepository userRepository, NoteMapper noteMapper, CacheManager cacheManager, NoteVersionRepository noteVersionRepository, NotePolicyService notePolicyService) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.noteMapper = noteMapper;
        this.noteCache = cacheManager.getCache("NOTE_CACHE");
        this.noteVersionRepository = noteVersionRepository;
        this.notePolicyService = notePolicyService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<NoteDto> fetchNotes(String actorEmail) {
        List<Note> notes = noteRepository.findByActorEmail(actorEmail);
        List<NoteDto> noteDtos = new ArrayList<>();
        if (notes.isEmpty()) return List.of();
        for (Note note : notes) {
            NoteAccessRole accessRole = notePolicyService.resolveRole(actorEmail, note);
            NoteDto noteDto = noteMapper.toDto(note, accessRole);
            noteDtos.add(noteDto);
        }
        return noteDtos;
    }

    @Override
    public NoteDto fetchNote(String actorEmail, UUID noteId) {
        Note note = notePolicyService.findNoteById(noteId);

        NoteAccessRole accessRole = notePolicyService.resolveRole(actorEmail, note);

        if (accessRole == null) {
            if (!note.getVisibility().equals(NoteVisibility.PUBLIC)) {
                log.warn("Note with id={} visibility is not public", noteId);
                throw new BadRequestException("Note visibility is not public");
            }
        }

        return noteMapper.toDto(note, accessRole);
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

        if (Helpers.isBlank(note.title()))
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
            Note saveNote = notePolicyService.findNoteById(note.id());

            NoteAccessRole accessRole = notePolicyService.resolveRole(actorEmail, saveNote);

            if (!accessRole.equals(NoteAccessRole.OWNER) && !accessRole.equals(NoteAccessRole.EDITOR)) {
                log.warn("User with this email={} does not have permission to save this note", actorEmail);
                throw new BadRequestException("User with this email does not have permission to save this note");
            }

            versionNumber = Optional.ofNullable(noteVersionRepository.findMaxVersionByNoteId(note.id()))
                    .map(v -> v + 1)
                    .orElse(1);

            NoteVersion newNoteVersion = noteVersionRepository.save(
                    new NoteVersion(
                            null,
                            saveNote,
                            note.title(),
                            note.content(),
                            user.getId(),
                            versionNumber
                    )
            );

            saveNote.setCurrentNoteVersion(newNoteVersion.getId());
            saveNote.getNoteVersions().add(newNoteVersion);
            noteRepository.save(saveNote);

            if (noteCache != null) {
                noteCache.evict(note.id());
            }
        }
        return note;
    }

    @Transactional
    @Override
    public NotePayloadDto updateNote(String actorEmail, NotePayloadDto note) {
        Note updateNote = notePolicyService.findNoteById(note.id());

        NoteAccessRole accessRole = notePolicyService.resolveRole(actorEmail, updateNote);

        if (!accessRole.equals(NoteAccessRole.OWNER) && !accessRole.equals(NoteAccessRole.EDITOR)) {
            log.warn("User with this email={} does not have permission to update this note", actorEmail);
            throw new BadRequestException("User with this email does not have permission to update this note");
        }

        if (noteCache != null) {
            noteCache.put(note.id(), note);
        }
        return note;
    }

    @Transactional
    @Override
    public void deleteNote(String actorEmail, UUID noteId) {
        Note note = notePolicyService.findNoteById(noteId);

        NoteAccessRole accessRole = notePolicyService.resolveRole(actorEmail, note);

        if (!accessRole.equals(NoteAccessRole.OWNER)) {
            log.warn("User with this email={} does not have permission to delete this note", actorEmail);
            throw new BadRequestException("User with this email does not have permission to delete this note");
        }

        noteRepository.delete(note);

        if (noteCache != null) {
            noteCache.evict(noteId);
        }
    }

    @Override
    public void changeNoteVisibility(String userEmail, UUID noteId, NoteVisibility visibility) {
        Note note = notePolicyService.findNoteById(noteId);

        NoteAccessRole accessRole = notePolicyService.resolveRole(userEmail, note);

        if (!accessRole.equals(NoteAccessRole.OWNER)) {
            log.warn("User with email={} is not allowed to change the visibility of this note", userEmail);
            throw new BadRequestException("User with this email is not allowed to change the visibility of this note");
        }

        note.setVisibility(visibility);
        noteRepository.save(note);
    }
}
