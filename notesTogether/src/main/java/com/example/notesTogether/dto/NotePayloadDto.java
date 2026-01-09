package com.example.notesTogether.dto;

import java.util.UUID;

public record NotePayloadDto(
        UUID id,
        String title,
        String content
) {
}
