package com.ndinhchien.m4y.domain.notification.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.notification.dto.NotificationResponseDto.INotification;
import com.ndinhchien.m4y.domain.notification.entity.Notification;
import com.ndinhchien.m4y.domain.notification.repository.NotificationRepository;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.domain.user.repository.UserRepository;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;
import com.ndinhchien.m4y.global.service.LinkService;
import com.ndinhchien.m4y.global.util.CommonUtils.InstantRange;
import com.ndinhchien.m4y.global.websocket.MessageDestination;
import com.ndinhchien.m4y.global.websocket.MessageManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MessageManager messageManager;
    private final UserRepository userRepository;
    private final LinkService linkService;

    @Transactional(readOnly = true)
    public List<INotification> getNotifications(User user, Instant start, Instant end) {
        return notificationRepository.findAllByUserAndCreatedAtGreaterThanAndCreatedAtLessThan(user,
                start,
                end);
    }

    @Transactional
    private Notification sendNotification(User user, String content) {
        Notification notification = new Notification(user, content);
        notification = notificationRepository.save(notification);
        messageManager.sendToUser(user, MessageDestination.PRIVATE_NOTIFICATION, notification);
        return notification;
    }

    @Transactional
    public List<Notification> markAsViewed(User user, List<Long> ids) {
        List<Notification> updated = new ArrayList<>();
        List<Notification> notifications = notificationRepository.findAllById(ids);
        for (Notification notification : notifications) {
            if (notification.isRecipient(user) && !notification.getIsViewed()) {
                notification.updateIsViewed();
                notification = notificationRepository.save(notification);
                updated.add(notification);
            }
        }

        return updated;
    }

    @Transactional
    public long hardDeleteNotificactions(User user, List<Long> ids) {
        return notificationRepository.deleteAllByUserAndIdIn(user, ids);
    }

    private Notification validateNotification(Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.NOTIFICATION_NOT_FOUND);
        });
    }

    private final String toUserHappyBirthday = """
                Happy birthday to you %s,
                We wish you good health and success in the coming year.
                With love,
            """;

    @Scheduled(cron = "10 0 0 * * *")
    public void sendBirthdayNotifications() {
        InstantRange range = new InstantRange(Instant.now());
        List<User> users = userRepository.findAllByBirthdayGreaterThanAndBirthdayLessThan(range.getStartOfDay(),
                range.getEndOfDay());
        for (User user : users) {
            String content = String.format(toUserHappyBirthday, user.getName());
            sendNotification(user, content);
        }
    }

    private final String toAdminNewRequest = """
                %s want to become a translator of your project (%s).
            """;

    public void sendToAdminNewRequest(Project project, User translator) {
        String link = linkService.getTranslatorRequestLink();
        String content = String.format(toAdminNewRequest, translator.getName(), link);
        User admin = project.getAdmin();
        sendNotification(admin, content);
    }

    private final String toUserRequestHandled = """
                Your request for project (%s) was just handled.
            """;

    public void sendToUserRequestHandled(Project project, User translator) {
        String link = linkService.getProjectLink(project);
        String content = String.format(toUserRequestHandled, link);
        sendNotification(translator, content);
    }

    private final String toUserTranslatorAdded = """
                You are added by %s as a translator of the project (%s).
            """;

    public void sendToUserTranslatorAdded(Project project, User translator) {
        String link = linkService.getProjectLink(project);
        User admin = project.getAdmin();
        String content = String.format(toUserTranslatorAdded, admin.getName(), link);
        sendNotification(translator, content);
    }

}
