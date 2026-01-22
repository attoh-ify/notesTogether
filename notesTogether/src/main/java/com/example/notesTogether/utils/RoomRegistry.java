package com.example.notesTogether.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomRegistry {
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    public void joinRoom(String noteId, WebSocketSession session) {
        rooms.computeIfAbsent(noteId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void leaveRoom(String noteId, WebSocketSession session) {
        Set<WebSocketSession> room = rooms.get(noteId);
        if(room != null) {
            room.remove(session);
            if (room.isEmpty()) {
                rooms.remove(noteId);
            }
        }
    }

    public boolean isEmpty(String noteId) {
        return !rooms.containsKey(noteId);
    }

    public Set<WebSocketSession> getRoom(String noteId) {
        return rooms.getOrDefault(noteId, Set.of());
    }
}
