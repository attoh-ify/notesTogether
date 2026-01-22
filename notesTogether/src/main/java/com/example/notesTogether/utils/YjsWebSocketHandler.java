//package com.example.notesTogether.utils;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.BinaryMessage;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.BinaryWebSocketHandler;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class YjsWebSocketHandler extends BinaryWebSocketHandler {
//    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        String room = getRoom(session);
//        rooms.computeIfAbsent(room, k -> ConcurrentHashMap.newKeySet())
//                .add(session);
//        System.out.println("Client joined room: " + room);
//    }
//
//    @Override
//    protected void handleBinaryMessage(
//            WebSocketSession session,
//            BinaryMessage message
//    ) throws Exception {
//        String room = getRoom(session);
//
//        for (WebSocketSession peer : rooms.getOrDefault(room, Set.of())) {
//            if (peer.isOpen() && peer != session) {
//                peer.sendMessage(message);
//            }
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(
//            WebSocketSession session,
//            CloseStatus status
//    ) {
//        rooms.values().forEach(set -> set.remove(session));
//    }
//
//    private String getRoom(WebSocketSession session) {
//        return UriComponentsBuilder
//                .fromUri(session.getUri())
//                .build()
//                .getQueryParams()
//                .getFirst("room");
//    }
//}
