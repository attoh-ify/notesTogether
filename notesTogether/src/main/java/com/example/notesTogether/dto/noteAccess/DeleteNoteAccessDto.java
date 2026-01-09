package com.example.notesTogether.dto.noteAccess;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Represents payload required to delete access to a note")
public record DeleteNoteAccessDto(
        @Schema(
                description = "Unique identifier of the note access entry",
                example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
        )
        UUID id,

        @Schema(
                description = "Unique identifier of the note the user has access to",
                example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
        )
        UUID noteId
) {}
