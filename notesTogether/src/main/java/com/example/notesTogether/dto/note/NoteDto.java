package com.example.notesTogether.dto.note;

import com.example.notesTogether.dto.noteVersion.NoteVersionDto;
import com.example.notesTogether.dto.noteAccess.NoteAccessDto;
import com.example.notesTogether.entities.NoteAccessRole;
import com.example.notesTogether.entities.NoteVisibility;
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
                description = "Unique identifier of the user that owns the note",
                example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
        )
        UUID userId,

        @Schema(
                description = "Visibility of the note, either private or public",
                example = "PRIVATE"
        )
        NoteVisibility visibility,

        @Schema(
                description = "List of user users with access to the note"
        )
        List<NoteAccessDto> noteAccesses,

        @Schema(
                description = "Current users role on the note",
                example = "VIEWER"
        )
        NoteAccessRole accessRole,

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