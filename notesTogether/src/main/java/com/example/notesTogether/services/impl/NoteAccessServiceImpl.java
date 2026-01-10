package com.example.notesTogether.services.impl;

import com.example.notesTogether.dto.noteAccess.AddNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.DeleteNoteAccessDto;
import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteAccess;
import com.example.notesTogether.entities.NoteAccessRole;
import com.example.notesTogether.entities.User;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.mappers.NoteAccessMapper;
import com.example.notesTogether.repositories.NoteAccessRepository;
import com.example.notesTogether.repositories.NoteRepository;
import com.example.notesTogether.repositories.UserRepository;
import com.example.notesTogether.services.NoteAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class NoteAccessServiceImpl implements NoteAccessService {
    private final NoteAccessRepository noteAccessRepository;
    private final NoteAccessMapper noteAccessMapper;
    private final UserRepository userRepository;
    private final NotePolicyService notePolicyService;

    private static final Logger log =
            LoggerFactory.getLogger(NoteAccessServiceImpl.class);

    public NoteAccessServiceImpl(NoteAccessRepository noteAccessRepository, NoteAccessMapper noteAccessMapper, UserRepository userRepository, NotePolicyService notePolicyService) {
        this.noteAccessRepository = noteAccessRepository;
        this.noteAccessMapper = noteAccessMapper;
        this.userRepository = userRepository;
        this.notePolicyService = notePolicyService;
    }

    @Transactional
    @Override
    public NoteAccessDto addAccess(String userEmail, AddNoteAccessDto noteAccess) {
        if (noteAccess.role().equals(NoteAccessRole.OWNER)) {
            log.warn("Owner role can not be assigned to another user");
            throw new BadRequestException("Owner role can not be assigned to another user");
        }

        Note note = notePolicyService.findNoteById(noteAccess.noteId());
        NoteAccessRole accessRole = notePolicyService.resolveRole(userEmail, note);

        if (!accessRole.equals(NoteAccessRole.OWNER)) {
            log.warn("User with the email={} is not the owner of this note", userEmail);
            throw new BadRequestException("User with the email is not the owner of this note");
        }

        if (noteAccess.email().equals(userEmail)) {
            log.warn("Owner already has access to this note");
            throw new BadRequestException("Owner already has access to this note");
        }

        User newAccessUser = userExists(noteAccess.email());

        try {
            return noteAccessMapper.toDto(
                    noteAccessRepository.save(
                            new NoteAccess(
                                    null,
                                    note,
                                    newAccessUser.getEmail(),
                                    noteAccess.role()
                            )
                    )
            );
        } catch (DataIntegrityViolationException e) {
            log.warn("Note access already exists for the email={}", noteAccess.email());
            throw new BadRequestException("Note access already exists for the provided email");
        }
    }

    @Transactional
    @Override
    public NoteAccessDto updateAccess(String userEmail, NoteAccessDto noteAccess) {
        notePolicyService.isOwner(userEmail, noteAccess.note().id());
        NoteAccess updateNoteAccess = noteAccessRepository.findById(noteAccess.id())
                .orElseThrow(() -> {
                    log.warn("Note access not found id={}", noteAccess.id());
                    return new BadRequestException(
                            "Note access with this id is not registered."
                    );
                });
        updateNoteAccess.setRole(noteAccess.role());
        return noteAccessMapper.toDto(noteAccessRepository.save(updateNoteAccess));
    }

    @Transactional
    @Override
    public void deleteAccess(String userEmail, DeleteNoteAccessDto noteAccess) {
        notePolicyService.isOwner(userEmail, noteAccess.noteId());
        noteAccessRepository.deleteById(noteAccess.id());
    }

    @Transactional(readOnly = true)
    @Override
    public List<NoteAccessDto> getAllAccess(String userEmail, UUID noteId) {
        notePolicyService.isOwner(userEmail, noteId);
        return noteAccessRepository.findByNoteId(noteId)
                .stream()
                .map(noteAccessMapper::toDto)
                .toList();
    }

    private User userExists(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.warn("User not found with email={}", userEmail);
                    return new BadRequestException("User not found with email");
                });
    }
}
