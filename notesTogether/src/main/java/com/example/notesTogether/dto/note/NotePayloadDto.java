package com.example.notesTogether.dto.note;

import java.util.UUID;

public record NotePayloadDto(
        UUID id,
        String title,
        String content
) {
}
