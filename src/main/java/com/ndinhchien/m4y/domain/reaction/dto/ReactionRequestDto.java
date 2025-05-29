package com.ndinhchien.m4y.domain.reaction.dto;

import com.ndinhchien.m4y.domain.proposal.type.ProposalType;
import com.ndinhchien.m4y.domain.reaction.type.Emoji;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ReactionRequestDto {

    @Getter
    public static class ReactToProposalDto {

        @NotBlank
        private String proposalName;

        @NotNull
        private ProposalType proposalType;

        private Emoji emoji;

        private Boolean isDeleted;
    }

    @Getter
    public static class ReactToProjectDto {

        @NotNull
        private Long projectId;

        private Emoji emoji;

        private Boolean isDeleted;
    }

    @Getter
    public static class ReactToCommentDto {

        @NotNull
        private Long commentId;

        @NotNull
        private Boolean isReply;

        private Emoji emoji;

        private Boolean isDeleted;
    }

    @Getter
    public static class ReactToMessageDto {

        @NotNull
        private Long messageId;

        private Emoji emoji;

        private Boolean isDeleted;
    }

}
