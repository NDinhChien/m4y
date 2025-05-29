package com.ndinhchien.m4y.domain.user.dto;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import com.ndinhchien.m4y.domain.notification.dto.NotificationResponseDto.INotification;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProject;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IRequest;
import com.ndinhchien.m4y.domain.user.type.UserRole;

public class UserResponseDto {

    public static interface IBasicUser {
        Long getId();

        String getUserName();

        Boolean getIsVerified();

        Boolean getIsBanned();

        UserRole getRole();

        String getAvatar();

        String getFullName();

        Instant getBirthday();

        String getBio();

        String getCountryName();

        String getDioceseName();

        String getDeaneryName();

        String getParishName();

        Instant getJoinedAt();

    }

    public static interface IPublicUser extends IBasicUser {

        List<IBasicProject> getProjects();
    }

    public static interface IUser extends IBasicUser {

        String getEmail();

        Instant getLastUserNameUpdate();

        Set<Long> getFollowers();

        Set<Long> getFollowings();

        List<IRequest> getRequests();

        List<INotification> getNotifications();
    }
}
