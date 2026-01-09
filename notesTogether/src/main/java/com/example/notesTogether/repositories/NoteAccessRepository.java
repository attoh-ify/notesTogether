package com.example.notesTogether.repositories;

import com.example.notesTogether.entities.NoteAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoteAccessRepository extends JpaRepository<NoteAccess, UUID> {
}
