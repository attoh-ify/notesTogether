package com.example.notesTogether.interceptors;

import com.example.notesTogether.services.impl.NotePolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;
import java.util.UUID;

public class StompAuthInterceptor implements ChannelInterceptor {
    private final NotePolicyService notePolicyService;

    private static final Logger log = LoggerFactory.getLogger(StompAuthInterceptor.class);

    public StompAuthInterceptor(NotePolicyService notePolicyService) {
        this.notePolicyService = notePolicyService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            Principal principal = accessor.getUser();

            if (destination == null || principal == null) {
                log.warn("Unauthenticated subscription attempt");
                throw new IllegalStateException("Unauthenticated subscription attempt");
            }

            if (destination.startsWith("/topic/public/")) {
                UUID noteId = extractNoteId(destination);
                String userEmail = principal.getName();

                log.debug("Authorizing SUBSCRIBE user={} noteId={}", userEmail, noteId);
                notePolicyService.isEditor(userEmail, noteId);
            }
        }
        return message;
    }

    private UUID extractNoteId(String destination) {
        try {
            String id = destination.substring("/topic/public/".length());
            return UUID.fromString(id);
        } catch (Exception e) {
            log.error("Invalid note topic destination");
            throw new IllegalArgumentException("Invalid note topic destination");
        }
    }
}
