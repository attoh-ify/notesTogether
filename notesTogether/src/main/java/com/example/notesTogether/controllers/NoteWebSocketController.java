package com.example.notesTogether.controllers;

import com.example.notesTogether.dto.note.NotePayloadDto;
import com.example.notesTogether.services.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "Notes WebSocket",
        description = "WebSocket endpoints for live note collaboration"
)
public class NoteWebSocketController {
        private final NoteService noteService;
        private final SimpMessagingTemplate messagingTemplate;

        public NoteWebSocketController(
                NoteService noteService,
                SimpMessagingTemplate messagingTemplate
        ) {
            this.noteService = noteService;
            this.messagingTemplate = messagingTemplate;
        }

    @MessageMapping("/note.addUser")
    @Operation(summary = "Add new user to live note editing")
    public void addUserToNoteLiveEdit(
            @Payload NotePayloadDto payload,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        headerAccessor.getSessionAttributes().put("actorEmail", payload.actorEmail());
        headerAccessor.getSessionAttributes().put("noteId", payload.noteId());
        NotePayloadDto result = noteService.addUserToLiveUpdate(payload);

        messagingTemplate.convertAndSend("/topic/public/" + payload.noteId(), result);
    }

    @MessageMapping("/note.saveNote")
    @Operation(summary = "Save a note")
    public void saveNote(
            @Payload NotePayloadDto payload
    ) {
        NotePayloadDto result = noteService.saveNote(payload);

        messagingTemplate.convertAndSend("/topic/public/" + payload.noteId(), result);
    }

    @MessageMapping("/note.updateNote")
    @Operation(summary = "Update a note")
    public void updateNote(
            @Payload NotePayloadDto payload
    ) {
        NotePayloadDto result = noteService.updateNote(payload);

        messagingTemplate.convertAndSend("/topic/public/" + payload.noteId(), result);
    }
}
