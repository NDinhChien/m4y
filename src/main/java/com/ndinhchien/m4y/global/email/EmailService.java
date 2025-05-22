package com.ndinhchien.m4y.global.email;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailService {
    private static final String NOREPLY_ADDRESS = "noreply@miracle4you";

    private final JavaMailSender emailSender;

    private final TextTemplate textTemplate;

    public void sendAccountVerification(User recipient, String token) throws BusinessException {
        String to = recipient.getEmail();
        String name = StringUtils.hasText(recipient.getFullName()) ? recipient.getFullName() : recipient.getUserName();
        String text = textTemplate.getAccountVerificationText(name, token);
        String subject = textTemplate.getAccountVerificationSubject();
        sendSimpleMessage(to, subject, text);
    }

    public void sendPasswordReset(User recipient, String token) throws BusinessException {
        String to = recipient.getEmail();
        String name = StringUtils.hasText(recipient.getFullName()) ? recipient.getFullName() : recipient.getUserName();
        String text = textTemplate.getPasswordResetText(name, token);
        String subject = textTemplate.getPasswordResetSubject();
        sendSimpleMessage(to, subject, text);
    }

    private void sendSimpleMessage(String to, String subject, String text) throws BusinessException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NOREPLY_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (Exception e) {
            log.info("Failed to send email: {}", e.getMessage());
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email");
        }
    }

}