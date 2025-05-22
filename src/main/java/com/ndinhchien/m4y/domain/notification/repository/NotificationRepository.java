package com.ndinhchien.m4y.domain.notification.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.notification.dto.NotificationResponseDto.INotification;
import com.ndinhchien.m4y.domain.notification.entity.Notification;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<INotification> findAllByUserAndCreatedAtGreaterThanAndCreatedAtLessThan(User user, Instant start, Instant end);

    long deleteAllByIdInAndUser(List<Long> ids, User user);

    long deleteAllByUser(User user);
}
