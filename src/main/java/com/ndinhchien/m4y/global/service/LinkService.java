package com.ndinhchien.m4y.global.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.user.entity.User;

@Component
public class LinkService {

    @Value("${client.url}")
    private String clientUrl;

    public String getAccountVerificationLink(String token) {
        return String.format(clientUrl + "/auth/verify?token=%s", token);
    }

    public String getPasswordResetLink(String token) {
        return String.format(clientUrl + "/auth/reset?token=%s", token);
    }

    public String getTranslatorRequestLink() {
        return String.format(clientUrl + "/projects?tab=2");
    }

    public String getUserLink(User user) {
        return String.format(clientUrl + "/user/id/%s", user.getId().toString());
    }

    public String getProjectLink(Project project) {
        return String.format(clientUrl + "/project/id/%s", project.getId().toString());
    }

    public String getGoogleRedirectUrl() {
        return String.format(clientUrl + "/google/redirect");
    }
}
