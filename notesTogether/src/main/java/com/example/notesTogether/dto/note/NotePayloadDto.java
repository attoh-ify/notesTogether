package com.example.notesTogether.dto.note;

import com.example.notesTogether.entities.WebsocketAction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Represents a note payload used by NotesTogether web socket, for updating and saving")
public record NotePayloadDto(
        @Schema(
                description = "Email of the user performing the action",
                example = "user@example.com"
        )
        String actorEmail,

        @Schema(
                description = "Unique identifier of the note",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
        )
        UUID noteId,

        @Schema(
                description = "Title of the note for update/save",
                example = "Project Meeting Notes"
        )
        String title,

        @Schema(
                description = "Content of the note in JSON format for update/save"
        )
        String content,

        @Schema(
                description = "Action to be performed on the Websocket",
                example = "SAVE"
        )
        WebsocketAction action
) {
}
