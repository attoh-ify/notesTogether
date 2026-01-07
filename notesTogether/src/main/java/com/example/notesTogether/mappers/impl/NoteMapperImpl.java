package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.NoteDto;
import com.example.notesTogether.entities.Note;
import com.example.notesTogether.mappers.NoteMapper;
import org.springframework.stereotype.Component;

@Component
public class NoteMapperImpl implements NoteMapper {
    @Override
    public Note fromDto(NoteDto noteDto) {
        return new Note(
                noteDto.id(),
                noteDto.title(),
                noteDto.contentJson(),
                noteDto.user(),
                noteDto.visibility(),
                noteDto.collaborators(),
                noteDto.viewers()
        );
    }

    @Override
    public NoteDto toDto(Note note) {
        return new NoteDto(
                note.getId(),
                note.getTitle(),
                note.getContentJson(),
                note.getUser(),
                note.getVisibility(),
                note.getCollaborators(),
                note.getViewers(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
