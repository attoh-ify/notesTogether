package com.example.notesTogether.mappers.impl;

import com.example.notesTogether.dto.NoteVersionDto;
import com.example.notesTogether.entities.NoteVersion;
import com.example.notesTogether.mappers.NoteVersionMapper;
import org.springframework.stereotype.Component;

@Component
public class NoteVersionMapperImpl implements NoteVersionMapper {
    @Override
    public NoteVersion fromDto(NoteVersionDto noteVersionDto) {
        return new NoteVersion(
                noteVersionDto.id(),
                null,
                noteVersionDto.title(),
                noteVersionDto.contentJson(),
                noteVersionDto.createdBy(),
                noteVersionDto.versionNumber()
        );
    }

    @Override
    public NoteVersionDto toDto(NoteVersion noteVersion) {
        return new NoteVersionDto(
                noteVersion.getId(),
                noteVersion.getTitle(),
                noteVersion.getContentJson(),
                noteVersion.getCreatedBy(),
                noteVersion.getVersionNumber(),
                noteVersion.getCreatedAt()
        );
    }
}
