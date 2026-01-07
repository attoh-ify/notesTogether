package com.example.notesTogether.dto;

import com.example.notesTogether.entities.NoteVersion;
import com.example.notesTogether.entities.NoteVisibility;
import com.example.notesTogether.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
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
                description = "User who owns the note"
        )
        User user,

        @Schema(
                description = "Visibility of the note, either private or public",
                example = "PRIVATE"
        )
        NoteVisibility visibility,

        @Schema(
                description = "List of collaborators (user ID's) associated with the note"
        )
        List<UUID> collaborators,

        @Schema(
                description = "List of viewers (user ID's) associated with the note"
        )
        List<UUID> viewers,

        @Schema(
                description = "Current note version UUID"
        )
        UUID currentNoteVersion,

        @Schema(
                description = "List of note versions associated with the note"
        )
        List<NoteVersionDto> noteVersions,

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