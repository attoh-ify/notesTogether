package com.example.notesTogether.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Represents a specific version of a note")
public record NoteVersionDto(
        @Schema(
                description = "Unique identifier of the note version",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
        )
        UUID id,

        @Schema(
                description = "Identifier of the note this version belongs to",
                example = "c9b1f8a0-3d15-4a12-bd5a-7c0d0e7b2f1f"
        )
        UUID noteId,

        @Schema(
                description = "Content of the note in JSON format for this version"
        )
        String contentJson,

        @Schema(
                description = "Identifier of the user who created this version",
                example = "3a6d8e27-4c2f-4b71-9e4a-91c9d2f5c611"
        )
        UUID createdBy,

        @Schema(
                description = "Version number of the note, starting from 1",
                example = "2"
        )
        Integer versionNumber,

        @Schema(
                description = "Timestamp when this version was created",
                example = "2026-01-07T11:45:00"
        )
        LocalDateTime createdAt
) {}