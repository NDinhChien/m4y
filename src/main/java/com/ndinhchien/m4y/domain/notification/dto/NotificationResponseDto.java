package com.ndinhchien.m4y.domain.notification.dto;

import java.time.Instant;

public class NotificationResponseDto {

    public static interface INotification {
        Long getId();

        Long getUserId();

        String getContent();

        Boolean getIsViewed();

        Instant getCreatedAt();
    }
}
