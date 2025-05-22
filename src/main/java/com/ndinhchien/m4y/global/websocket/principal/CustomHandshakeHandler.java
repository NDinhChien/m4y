package com.ndinhchien.m4y.global.websocket.principal;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        Principal principal = request.getPrincipal();
        String name = null;
        if (principal == null) {
            name = UUID.randomUUID().toString();
        } else if (principal instanceof UserDetailsImpl) {
            name = ((UserDetailsImpl) principal).getUser().getEmail();
        } else {
            name = principal.toString();
        }
        return new StompPrincipal(name);
    }
}