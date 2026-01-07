package com.example.notesTogether.dto;

import com.example.notesTogether.entities.NoteAccessRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Represents access rights of a user to a specific note")
public record NoteAccessDto(
        @Schema(
                description = "Unique identifier of the note access entry",
                example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
        )
        UUID id,

        @Schema(
                description = "Identifier of the note to which access is granted",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
        )
        UUID noteId,

        @Schema(
                description = "Identifier of the user who has access to the note",
                example = "c9b1f8a0-3d15-4a12-bd5a-7c0d0e7b2f1f"
        )
        UUID userId,

        @Schema(
                description = "Role of the user for this note, determining their permissions",
                example = "EDITOR"
        )
        NoteAccessRole role
) {}