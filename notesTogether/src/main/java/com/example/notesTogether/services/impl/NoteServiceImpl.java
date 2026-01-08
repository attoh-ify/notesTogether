package com.example.notesTogether.services.impl;

import com.example.notesTogether.dto.NoteDto;
import com.example.notesTogether.entities.*;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.mappers.NoteMapper;
import com.example.notesTogether.repositories.NoteRepository;
import com.example.notesTogether.repositories.UserRepository;
import com.example.notesTogether.services.NoteService;
import com.example.notesTogether.utils.helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteMapper noteMapper;

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

    public NoteServiceImpl(NoteRepository noteRepository, UserRepository userRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.noteMapper = noteMapper;
    }

    @Override
    public Note createNote(UUID userId, Note note, String noteContent) {
        validateNote(note);
        final Integer versionNumber = 1;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found id={}", userId);
                    return new BadRequestException(
                            "User with this ID is not registered."
                    );
                });

        Note newNote = noteRepository.save(
                new Note(
                        null,
                        note.getTitle(),
                        user,
                        NoteVisibility.PUBLIC,
                        null,
                        null,
                        null
                )
        );

        NoteVersion firstNoteVersion = new NoteVersion(
                null,
                newNote,
                noteContent,
                userId,
                versionNumber
                );

        newNote.setCurrentNoteVersion(firstNoteVersion.getId());
        newNote.setNoteVersions(List.of(firstNoteVersion));
        return newNote;
    }

    @Override
    public List<NoteDto> fetchNotes(String userEmail) {
        List<Note> notes = noteRepository.findByActorEmail(userEmail);
        List<NoteDto> noteDtos = new ArrayList<>();
        if (notes.isEmpty()) return null;
        for (Note note : notes) {
            NoteAccessRole accessRole = NoteAccessRole.OWNER;
            if (!note.getUser().getEmail().equals(userEmail)) {
                for (NoteAccess noteAccess: note.getNoteAccesses()) {
                    if (noteAccess.getEmail().equals(userEmail)) {
                        accessRole = noteAccess.getRole();
                    }
                }
                note.setNoteAccesses(null);
            }
            NoteDto noteDto = noteMapper.toDto(note, accessRole);
            noteDtos.add(noteDto);
        }
        return noteDtos;
    }

    @Override
    public NoteDto updateNote(String userEmail, UUID noteId) {
        return null;
    }

    @Override
    public NoteDto saveNote(String userEmail, UUID noteId) {
        return null;
    }

    @Override
    public void deleteNote(UUID userId, UUID noteId) {

    }

    private void validateNote(Note note) {
        log.debug("Validating note");

        if (note == null)
            throw new BadRequestException("Note payload is required");
        if (note.getId() != null)
            throw new BadRequestException("Note ID is system generated");
        if (helpers.isBlank(note.getTitle()))
            throw new BadRequestException("Note title is required");
        if (note.getCurrentNoteVersion() != null)
            throw new BadRequestException("Current note version is system managed");
        if (note.getNoteVersions() != null && !note.getNoteVersions().isEmpty())
            throw new BadRequestException("Note versions are system managed");
    }
}
