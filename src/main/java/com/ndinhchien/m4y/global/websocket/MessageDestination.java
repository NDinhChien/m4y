package com.ndinhchien.m4y.global.websocket;

public enum MessageDestination {
    GLOBAL_MESSAGE("/topic/message/global"),
    PRIVATE_ERROR("/queue/error/"),
    PRIVATE_NOTIFICATION("/queue/notification/");

    public final String value;

    private MessageDestination(String value) {
        this.value = value;
    }
}
