package com.example.notesTogether.mappers;

import com.example.notesTogether.dto.NoteDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.entities.NoteAccessRole;

public interface NoteMapper {
    Note fromDto(NoteDto noteDto);
    NoteDto toDto(Note note, NoteAccessRole accessRole);
}
