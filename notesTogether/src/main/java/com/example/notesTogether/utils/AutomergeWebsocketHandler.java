package com.example.notesTogether.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.io.IOException;

@Component
public class AutomergeWebsocketHandler extends BinaryWebSocketHandler {
    private final RoomRegistry roomRegistry;
    private final SnapShotStore snapShotStore;
    private final ObjectMapper objectMapper;

    public AutomergeWebsocketHandler(RoomRegistry roomRegistry, SnapShotStore snapShotStore, ObjectMapper objectMapper) {
        this.roomRegistry = roomRegistry;
        this.snapShotStore = snapShotStore;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String noteId = getNoteId(session);

        if (roomRegistry.isEmpty(noteId)) {
            try {
                BinaryMessage binaryMessage = new BinaryMessage(snapShotStore.load(noteId));
                session.sendMessage(binaryMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        roomRegistry.joinRoom(noteId, session);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        String noteId = getNoteId(session);
        Set<WebSocketSession> room = roomRegistry.getRoom(noteId);
        System.out.println("Note id: " + noteId);
        System.out.println("Room: " + room);
        System.out.println("Message: " + message);
        byte[] payload = message.getPayload().array();
        System.out.println("Raw bytes length: " + payload.length);

        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < Math.min(payload.length, 32); i++) {
            hex.append(String.format("%02X ", payload[i]));
        }
        System.out.println("First 32 bytes: " + hex);

        for (WebSocketSession peer : room) {
            if (peer.isOpen()) {
                peer.sendMessage(message);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode json = objectMapper.readTree(message.getPayload());
            if (!"snapshot".equals(json.get("type").asText())) {
                return;
            }

            String noteId = json.get("noteId").asText();

            byte[] snapshot = objectMapper.convertValue(json.get("payload"), byte[].class);
            snapShotStore.save(noteId, snapshot);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String noteId = getNoteId(session);
        roomRegistry.leaveRoom(noteId, session);
        System.out.println("User left the room.");
    }

    private String getNoteId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return "default";

        String query = uri.getQuery();;
        if (query == null) return "default";

        return Arrays.stream(query.split("&"))
                .map(p -> p.split("="))
                .filter(p -> p.length == 2 && p[0].equals("noteId"))
                .map(p -> p[1])
                .findFirst()
                .orElse("default");
    }
}
