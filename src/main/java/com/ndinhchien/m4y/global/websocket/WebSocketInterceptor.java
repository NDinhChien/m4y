package com.ndinhchien.m4y.global.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ndinhchien.m4y.domain.auth.service.JwtService;
import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.ErrorMessage;
import com.ndinhchien.m4y.global.exception.TokenException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                setAuthenticate(accessor);
                log.info("New auth connection.");
            } catch (Exception e) {
                log.info("New unauth connection.");
            }
        }
        return message;
    }

    private void setAuthenticate(StompHeaderAccessor accessor) throws TokenException {
        String bearerToken = accessor.getFirstNativeHeader(JwtService.AUTHORIZATION_HEADER);

        String accessToken = jwtService.getAccessTokenFromHeader(bearerToken);

        if (!StringUtils.hasText(accessToken)) {
            throw new TokenException(ErrorMessage.ACCESS_TOKEN_NOT_FOUND);
        }

        User user = jwtService.validateToken(accessToken, "access");

        Authentication authentication = createAuthentication(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        accessor.setUser(authentication);
    }

    private Authentication createAuthentication(User user) {
        UserDetails userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}