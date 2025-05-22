package com.ndinhchien.m4y.global.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ndinhchien.m4y.global.service.LinkService;

@Component
public class TextTemplate {

    @Autowired
    private LinkService linkService;

    private final String accountVerificationSubject = "Verify Your Account";

    private final String accountVerificationTemplateText = """
                Welcome %s to Miracle4You,
                One final step to complete registration,
                Please follow this link: %s
                Thanks.
            """;

    private final String passwordResetSubject = "Reset Your Password";

    private final String passwordResetTemplateText = """
                Hi %s,
                To reset your password, please follows this link: %s
                If you did not request, you can safely ignore this email.
                Thanks.
            """;

    public String getAccountVerificationSubject() {
        return this.accountVerificationSubject;
    }

    public String getAccountVerificationText(String name, String token) {
        String link = linkService.getAccountVerificationLink(token);
        return String.format(accountVerificationTemplateText, name, link);
    }

    public String getPasswordResetSubject() {
        return this.passwordResetSubject;
    }

    public String getPasswordResetText(String name, String token) {
        String link = linkService.getPasswordResetLink(token);
        return String.format(passwordResetTemplateText, name, link);
    }
}
