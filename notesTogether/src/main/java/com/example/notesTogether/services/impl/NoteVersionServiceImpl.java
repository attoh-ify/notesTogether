package com.example.notesTogether.services.impl;

import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteVersion;
import com.example.notesTogether.exceptions.BadRequestException;
import com.example.notesTogether.repositories.NoteRepository;
import com.example.notesTogether.repositories.NoteVersionRepository;
import com.example.notesTogether.services.NoteVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NoteVersionServiceImpl implements NoteVersionService {
    private final NoteRepository noteRepository;
    private final NoteVersionRepository noteVersionRepository;
    private final NotePolicyService notePolicyService;

    private static final Logger log =
            LoggerFactory.getLogger(NoteVersionServiceImpl.class);

    public NoteVersionServiceImpl(NoteRepository noteRepository, NoteVersionRepository noteVersionRepository, NotePolicyService notePolicyService) {
        this.noteRepository = noteRepository;
        this.noteVersionRepository = noteVersionRepository;
        this.notePolicyService = notePolicyService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<NoteVersion> fetchAllVersions(String actorEmail, UUID noteId) {
        notePolicyService.isEditor(actorEmail, noteId);
        return noteVersionRepository.findByNoteIdOrderByVersionNumberAsc(noteId);
    }

    @Transactional(readOnly = true)
    @Override
    public NoteVersion fetchVersion(String actorEmail, UUID noteId, UUID noteVersionId) {
        notePolicyService.isEditor(actorEmail, noteId);
        return noteVersionRepository.findByIdAndNote_Id(noteVersionId, noteId)
                .orElseThrow(() -> {
                    log.warn("Note version with id={} not found", noteVersionId);
                    return new BadRequestException("Note version not found");
                });
    }

    @Transactional
    @Override
    public NoteVersion restoreVersion(String actorEmail, UUID noteId, UUID noteVersionId) {
        Note note = notePolicyService.isEditor(actorEmail, noteId);
        NoteVersion noteVersion = noteVersionRepository.findById(noteVersionId)
                .orElseThrow(() -> {
                    log.warn("Note version not found id={}", noteVersionId);
                    return new BadRequestException(
                            "Note version with this id does not exist."
                    );
                });

        if (!noteVersion.getNote().equals(note)) {
            log.warn("Note and Note version conflicts");
            throw new BadRequestException("Note and Note version conflicts");
        }

        note.setCurrentNoteVersion(noteVersionId);
        noteRepository.save(note);
        return noteVersionRepository.save(noteVersion);
    }
}
