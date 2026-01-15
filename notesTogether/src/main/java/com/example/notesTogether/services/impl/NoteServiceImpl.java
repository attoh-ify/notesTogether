package com.example.notesTogether.services.impl;

import com.example.notesTogether.dto.note.NoteDto;
import com.example.notesTogether.dto.note.NotePayloadDto;
import com.example.notesTogether.entities.*;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.repositories.NoteRepository;
import com.example.notesTogether.repositories.NoteVersionRepository;
import com.example.notesTogether.services.NoteService;
import com.example.notesTogether.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final Cache noteCache;
    private final NoteVersionRepository noteVersionRepository;
    private final NotePolicyService notePolicyService;
    private final UserPolicyService userPolicyService;

    private static final Logger log =
            LoggerFactory.getLogger(NoteServiceImpl.class);

    public NoteServiceImpl(NoteRepository noteRepository, NoteMapper noteMapper, CacheManager cacheManager, NoteVersionRepository noteVersionRepository, NotePolicyService notePolicyService, UserPolicyService userPolicyService) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.noteCache = cacheManager.getCache("NOTE_CACHE");
        this.noteVersionRepository = noteVersionRepository;
        this.notePolicyService = notePolicyService;
        this.userPolicyService = userPolicyService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<NoteDto> fetchNotes(String actorEmail) {
        List<Note> notes = noteRepository.findByActorEmail(actorEmail);
        List<NoteDto> noteDtos = new ArrayList<>();
        if (notes.isEmpty()) return List.of();
        for (Note note : notes) {
            NoteDto noteDto = noteMapper.toDto(note);
            noteDtos.add(noteDto);
        }
        return noteDtos;
    }

    @Override
    public NoteDto fetchNote(String actorEmail, UUID noteId) {
        Note note = notePolicyService.findNoteById(noteId);

        if (notePolicyService.resolveRole(actorEmail, note) == null) {
            if (!note.getVisibility().equals(NoteVisibility.PUBLIC)) {
                log.warn("Note with id={} visibility is not public", noteId);
                throw new BadRequestException("Note visibility is not public");
            }
        }

        return noteMapper.toDto(note);
    }

    @Transactional
    @Override
    public NoteDto createNote(String actorEmail, NotePayloadDto note) {
        User user = userPolicyService.userExists(actorEmail);

        if (Helpers.isBlank(note.title()))
            throw new BadRequestException("Note title is required");

        Note newNote = new Note(
                null,
                user,
                NoteVisibility.PUBLIC,
                null,
                null,
                null
        );
        noteRepository.save(newNote);

        NoteVersion firstNoteVersion = new NoteVersion(
                null,
                newNote,
                note.title(),
                note.content(),
                user.getId(),
                1
        );
        noteVersionRepository.save(firstNoteVersion);

        newNote.setCurrentNoteVersion(firstNoteVersion.getId());
        newNote.setNoteVersions(new ArrayList<>());
        newNote.getNoteVersions().add(firstNoteVersion);

        noteRepository.save(newNote);

//        if (noteCache != null) {
//            noteCache.evict(note.noteId());
//        }
        return noteMapper.toDto(newNote);
    }

    @Transactional
    @Override
    public NotePayloadDto saveNote(NotePayloadDto note) {
        User user = userPolicyService.userExists(note.actorEmail());

        if (Helpers.isBlank(note.title()))
            throw new BadRequestException("Note title is required");

        Note saveNote = notePolicyService.isEditor(note.actorEmail(), note.noteId());

        int versionNumber = Optional.ofNullable(noteVersionRepository.findMaxVersionByNoteId(note.noteId()))
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

//        if (noteCache != null) {
//            noteCache.evict(note.noteId());
//        }
        return note;
    }

    @Transactional
    @Override
    public NotePayloadDto updateNote(NotePayloadDto note) {
        notePolicyService.isEditor(note.actorEmail(), note.noteId());

//        if (noteCache != null) {
//            noteCache.put(note.noteId(), note);
//        }
        return note;
    }

    @Override
    public NotePayloadDto addUserToLiveUpdate(NotePayloadDto note) {
        Note currentNote = notePolicyService.isEditor(note.actorEmail(), note.noteId());
        NoteVersion currentNoteVersion = noteVersionRepository.findById(currentNote.getCurrentNoteVersion())
                .orElseThrow(() -> {
                    log.warn("Note version not found for id={}", currentNote.getCurrentNoteVersion());
                    return new BadRequestException("Note version not found");
                });

        NotePayloadDto payload;

        if (noteCache != null) {
            payload = noteCache.get(note.noteId(), NotePayloadDto.class);
            if (payload != null) {
                return new NotePayloadDto(
                        note.actorEmail(),
                        note.noteId(),
                        payload.title(),
                        payload.content(),
                        WebsocketAction.JOIN);
            }

        }
        return new NotePayloadDto(
                note.actorEmail(),
                note.noteId(),
                currentNoteVersion.getTitle(),
                currentNoteVersion.getContentJson(),
                WebsocketAction.JOIN);
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

//        if (noteCache != null) {
//            noteCache.evict(noteId);
//        }
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
