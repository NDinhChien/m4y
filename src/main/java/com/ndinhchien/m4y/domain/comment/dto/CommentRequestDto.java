package com.ndinhchien.m4y.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class CommentRequestDto {

    @Getter
    public static class AddCommentDto {

        @NotNull
        private Long projectId;

        @NotBlank
        private String content;

    }

    @Getter
    public static class AddReplyDto {

        @NotNull
        private Long commentId;

        @NotBlank
        private String content;

    }

    @Getter
    public static class UpdateCommentDto {

        @NotNull
        private Long commentId;

        @NotNull
        private Boolean isReply;

        private String content;

        private Boolean isDeleted;
    }
}
