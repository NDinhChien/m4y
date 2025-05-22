package com.ndinhchien.m4y.domain.reaction.dto;

public class ReactionResponseDto {
    public static interface IBaseReaction {
        Long getUserId();

        String getEmoji();

        Boolean getIsDeleted();
    }

    public static interface IProjectReaction extends IBaseReaction {
        Long getId();

        Long getProjectId();

    }

    public static interface IMessageReaction extends IBaseReaction {
        Long getId();

        Long getMessageId();

    }
}
