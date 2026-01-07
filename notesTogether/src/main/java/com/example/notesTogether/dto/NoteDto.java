package com.example.notesTogether.dto;

import com.example.notesTogether.entities.NoteVisibility;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Represents a note returned by the NotesTogether service")
public record NoteDto(
        @Schema(
                description = "Unique identifier of the note",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
        )
        UUID id,

        @Schema(
                description = "Title of the note",
                example = "Project Meeting Notes"
        )
        String title,

        @Schema(
                description = "Content of the note in JSON format, representing blocks, metadata, and formatting"
        )
        String contentJson,

        @Schema(
                description = "Identifier of the user who owns the note",
                example = "c9b1f8a0-3d15-4a12-bd5a-7c0d0e7b2f1f"
        )
        UUID ownerId,

        @Schema(
                description = "Visibility of the note, either private or public",
                example = "PRIVATE"
        )
        NoteVisibility visibility,

        @Schema(
                description = "Timestamp when the note was created",
                example = "2026-01-07T11:15:30"
        )
        LocalDateTime createdAt,

        @Schema(
                description = "Timestamp when the note was last updated",
                example = "2026-01-07T11:45:00"
        )
        LocalDateTime updatedAt
) {}