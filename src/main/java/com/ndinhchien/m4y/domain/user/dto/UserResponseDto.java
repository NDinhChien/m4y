package com.ndinhchien.m4y.domain.user.dto;

import java.time.Instant;
import java.util.List;

import com.ndinhchien.m4y.domain.notification.dto.NotificationResponseDto.INotification;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProject;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.ICommentReaction;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IMessageReaction;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IProjectReaction;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IProposalReaction;
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

        Instant getUserNameUpdatedAt();

        List<IFollower> getFollowers();

        List<IFollowing> getFollowings();

        List<INotification> getNotifications();

        List<IProjectReaction> getProjectReactions();

        List<IProposalReaction> getProposalReactions();

        List<ICommentReaction> getCommentReactions();

        List<IMessageReaction> getMessageReactions();
    }

    public static interface IFollowing {
        Long getId();

        Long getUserId();

        Long getTargetId();

        IBasicUser getTarget();

        Instant getCreatedAt();
    }

    public static interface IFollower {
        Long getId();

        Long getUserId();

        IBasicUser getUser();

        Long getTargetId();

        Instant getCreatedAt();
    }
}
