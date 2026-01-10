package com.example.notesTogether.listener;

import com.example.notesTogether.dto.note.NotePayloadDto;
import com.example.notesTogether.entities.WebsocketAction;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;

    private static final Logger log =
            LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String actorEmail = (String) headerAccessor.getSessionAttributes().get("actorEmail");
        UUID noteId = (UUID) headerAccessor.getSessionAttributes().get("noteId");
        if (actorEmail != null) {
            log.info("User disconnected: {}", actorEmail);
            messagingTemplate.convertAndSend("/topic/public/" + noteId, new NotePayloadDto(actorEmail, noteId, null, null, WebsocketAction.LEAVE));
        }
    }
}
