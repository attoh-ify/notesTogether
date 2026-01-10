package com.example.notesTogether.repositories;

import com.example.notesTogether.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
    @Query("""
            SELECT DISTINCT n FROM Note n
            LEFT JOIN NoteAccess na ON na.note = n
            WHERE n.user.email = :actorEmail
                OR na.email = :actorEmail
            """)
    List<Note> findByActorEmail(String actorEmail);
}
