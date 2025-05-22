package com.ndinhchien.m4y.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.ndinhchien.m4y.global.websocket.WebSocketInterceptor;
import com.ndinhchien.m4y.global.websocket.exception.CustomStompExceptionHandler;
import com.ndinhchien.m4y.global.websocket.principal.CustomHandshakeHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketInterceptor webSocketInterceptor;
    private final CustomStompExceptionHandler webSocketExceptionHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry
                .setErrorHandler(webSocketExceptionHandler)
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketInterceptor);
    }
}