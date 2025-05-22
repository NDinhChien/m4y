package com.ndinhchien.m4y.domain.comment.dto;

import java.time.Instant;
import java.util.List;

public class CommentResponseDto {
    public static interface IBaseComment {
        Long getId();

        Long getUserId();

        String getContent();

        Integer getReactCount();

        Instant getEditedAt();

        Instant getCreatedAt();

        Boolean getIsDeleted();
    }

    public static interface IReply extends IBaseComment {
        Long getCommentId();
    }

    public static interface IComment extends IBaseComment {
        Long getId();

        Long getProjectId();

        List<IReply> getReplies();
    }
}
