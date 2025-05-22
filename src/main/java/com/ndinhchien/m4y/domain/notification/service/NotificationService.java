package com.ndinhchien.m4y.domain.notification.service;

import java.time.Instant;
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
import com.ndinhchien.m4y.global.service.MessageManager;
import com.ndinhchien.m4y.global.util.CommonUtils.InstantRange;
import com.ndinhchien.m4y.global.websocket.MessageDestination;

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
    public List<INotification> getNotifications(User user, Instant date) {
        InstantRange range = new InstantRange(date);
        return notificationRepository.findAllByUserAndCreatedAtGreaterThanAndCreatedAtLessThan(user,
                range.getStartOfDay(),
                range.getEndOfDay());
    }

    @Transactional
    private Notification sendNotification(User user, String content) {
        Notification notification = new Notification(user, content);
        notification = notificationRepository.save(notification);
        messageManager.sendToUser(user, MessageDestination.PRIVATE_NOTIFICATION, notification);
        return notification;
    }

    @Transactional
    public Notification markAsRead(User user, Long notificationId) {
        Notification notification = validateNotification(notificationId);
        if (notification.isRecipient(user) && !notification.getIsRead()) {
            notification.updateIsRead();
            return notificationRepository.save(notification);
        }
        return null;
    }

    @Transactional
    public long hardDeleteAll(User user) {
        return notificationRepository.deleteAllByUser(user);
    }

    private Notification validateNotification(Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.NOTIFICATION_NOT_FOUND);
        });
    }

    private final String happyBirthdayToYou = """
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
            String content = String.format(happyBirthdayToYou, user.getName());
            sendNotification(user, content);
        }
    }

    private final String newTranslatorRequestComming = """
                %s want to become a translator of your project (%s).
            """;

    public void send_ToAdmin_NewTranslatorRequestComming_Noti(Project project, User translator) {
        String link = linkService.getTranslatorRequestLink(project, translator);
        String content = String.format(newTranslatorRequestComming, translator.getName(), link);
        User admin = project.getAdmin();
        sendNotification(admin, content);
    }

    private final String translatorRequestAccepted = """
                Your request are accepted, you can start editing subtitles of project (%s).
            """;

    public void send_ToTranslator_TranslatorRequestAccepted_Notif(Project project, User translator) {
        String link = linkService.getProjectLink(project);
        String content = String.format(translatorRequestAccepted, link);
        sendNotification(translator, content);
    }

    private final String beingAddedAsTranslator = """
                You are added by %s as a translator of the project (%s).
            """;

    public void send_ToTranslator_BeingAddedAsTranslator_Noti(Project project, User translator) {
        String link = linkService.getProjectLink(project);
        User admin = project.getAdmin();
        String content = String.format(beingAddedAsTranslator, admin.getName(), link);
        sendNotification(translator, content);
    }

}
