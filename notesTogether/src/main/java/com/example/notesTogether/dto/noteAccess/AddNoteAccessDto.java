package com.example.notesTogether.dto.noteAccess;

import com.example.notesTogether.entities.NoteAccessRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Represents payload required to create a new access to users note")
public record AddNoteAccessDto(
        @Schema(
                description = "Unique identifier of the note the user has access to",
                example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
        )
        UUID noteId,

        @Schema(
                description = "Email of the user who has access to the note",
                example = "user@example.com"
        )
        String email,

        @Schema(
                description = "Role of the user for this note, determining their permissions",
                example = "EDITOR"
        )
        NoteAccessRole role
) {}
