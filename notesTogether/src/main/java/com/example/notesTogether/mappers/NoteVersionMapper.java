package com.example.notesTogether.mappers;

import com.example.notesTogether.dto.NoteVersionDto;
import com.example.notesTogether.entities.NoteVersion;

public interface NoteVersionMapper {
    NoteVersion fromDto(NoteVersionDto version);
    NoteVersionDto toDto(NoteVersion version);
}
