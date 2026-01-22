package com.example.notesTogether.utils;

import com.example.notesTogether.entities.NoteVersion;
import com.example.notesTogether.repositories.NoteVersionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class SnapShotStore {
    private final NoteVersionRepository noteVersionRepository;

    public SnapShotStore(NoteVersionRepository noteVersionRepository) {
        this.noteVersionRepository = noteVersionRepository;
    }

    @Transactional
    public void save(String noteId, byte[] snapshot) {
        NoteVersion noteVersion = noteVersionRepository.findByNoteIdOrderByVersionNumberAsc(UUID.fromString(noteId)).get(0);
        noteVersion.setContentJson(snapshot);
        noteVersionRepository.save(noteVersion);
    }

    @Transactional
    public byte[] load(String noteId) {
        byte[] noteContent = noteVersionRepository.findByNoteIdOrderByVersionNumberAsc(UUID.fromString(noteId)).get(0).getContentJson();
        System.out.println("noteContent: " + noteContent);
        return noteContent;
    }
}
