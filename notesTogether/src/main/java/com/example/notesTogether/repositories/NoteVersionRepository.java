package com.example.notesTogether.repositories;

import com.example.notesTogether.entities.NoteVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoteVersionRepository extends JpaRepository<NoteVersion, UUID> {
    @Query("SELECT MAX(nv.version) FROM NoteVersion nv WHERE nv.note.id = :noteId")
    Integer findMaxVersionByNoteId(@Param("noteId") UUID noteId);
}
