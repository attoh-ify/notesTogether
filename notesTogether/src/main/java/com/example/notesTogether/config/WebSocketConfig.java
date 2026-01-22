package com.example.notesTogether.config;

import com.example.notesTogether.utils.AutomergeWebsocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final AutomergeWebsocketHandler handler;

    public WebSocketConfig(AutomergeWebsocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(handler, "/ws/automerge")
                .setAllowedOrigins("*");
    }
}


//import com.example.notesTogether.utils.YjsWebSocketHandler;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//    private final YjsWebSocketHandler handler;
//
//    public WebSocketConfig(YjsWebSocketHandler handler) {
//        this.handler = handler;
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry
//                .addHandler(handler, "/yjs")
//                .setAllowedOrigins("*");
//    }
//}

//import com.example.notesTogether.interceptors.StompAuthInterceptor;
//import com.example.notesTogether.services.JwtService;
//import com.example.notesTogether.services.impl.NotePolicyService;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    private final NotePolicyService notePolicyService;
//    private final JwtService jwtService;
//    private final ApplicationContext context;
//
//    public WebSocketConfig(NotePolicyService notePolicyService, JwtService jwtService, ApplicationContext context) {
//        this.notePolicyService = notePolicyService;
//        this.jwtService = jwtService;
//        this.context = context;
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws").withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/app");
//        registry.enableSimpleBroker("/topic");
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new StompAuthInterceptor(notePolicyService, jwtService, context));
//    }
//}
