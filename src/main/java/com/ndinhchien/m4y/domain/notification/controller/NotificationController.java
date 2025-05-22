package com.ndinhchien.m4y.domain.notification.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.notification.dto.NotificationResponseDto.INotification;
import com.ndinhchien.m4y.domain.notification.entity.Notification;
import com.ndinhchien.m4y.domain.notification.service.NotificationService;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "notification", description = "Notification Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get notification by date")
    @GetMapping
    public BaseResponse<List<INotification>> getNotificationsByDate(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant date) {
        return BaseResponse.success("Notifications", notificationService.getNotifications(userDetails.getUser(), date));
    }

    @Operation(summary = "Mark notification as read")
    @PutMapping
    public BaseResponse<Notification> markAsRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long notificationId) {

        return BaseResponse.success("Mark as read",
                notificationService.markAsRead(userDetails.getUser(), notificationId));
    }

    @Operation(summary = "Clear all notifications")
    @DeleteMapping("/all")
    public BaseResponse<Long> hardDeleteAll(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return BaseResponse.success("Hard delete all",
                notificationService.hardDeleteAll(userDetails.getUser()));
    }
}
