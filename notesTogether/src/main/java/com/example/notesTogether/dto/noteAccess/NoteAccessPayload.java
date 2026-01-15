package com.example.notesTogether.dto.noteAccess;

import com.example.notesTogether.entities.NoteAccessRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents payload required to create a new access to users note")
public record NoteAccessPayload(
        @Schema(
                description = "Email of the user you want to give access to the note",
                example = "user@example.com"
        )
        String email,

        @Schema(
                description = "Role of the user for this note, determining their permissions",
                example = "EDITOR"
        )
        NoteAccessRole role
) {}
