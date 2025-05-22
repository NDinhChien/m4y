package com.ndinhchien.m4y.global.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.websocket.MessageDestination;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE + 99)
@Component
public class MessageManager {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void addUser(User user) {
        users.putIfAbsent(user.getId(), user);
    }

    public void removeUser(User user) {
        users.remove(user.getId());
    }

    public Collection<User> getOnlineUsers() {
        return users.values();
    }

    public int getTotalOnlineUsers() {
        return users.size();
    }

    private boolean isOnline(User user) {
        return users.get(user.getId()) != null;
    }

    public void sendToUser(User user, MessageDestination destination, Object payload) {
        if (isOnline(user)) {
            sendToUser(user.getEmail(), destination, payload);
        }
    }

    public void sendToUser(String email, MessageDestination destination, Object payload) {
        if (destination.value.startsWith("/queue")) {
            messagingTemplate.convertAndSendToUser(email, destination.value, payload);
        }
    }

    public void sendToGlobal(MessageDestination destination, Object payload) {
        if (destination.value.startsWith("/topic")) {
            messagingTemplate.convertAndSend(destination.value, payload);
        }

    }

}