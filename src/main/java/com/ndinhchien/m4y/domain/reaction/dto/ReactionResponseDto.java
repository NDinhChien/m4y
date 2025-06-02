package com.ndinhchien.m4y.domain.reaction.dto;

import com.ndinhchien.m4y.domain.proposal.type.ProposalType;

public class ReactionResponseDto {

    public static interface IBaseReaction {
        Long getUserId();

        String getEmoji();

        Boolean getIsDeleted();
    }

    public static interface IProposalReaction extends IBaseReaction {
        Long getId();

        String getProposalName();

        ProposalType getProposalType();

    }

    public static interface IProjectReaction extends IBaseReaction {

        Long getId();

        Long getProjectId();

    }

    public static interface IMessageReaction extends IBaseReaction {

        Long getId();

        Long getMessageId();

    }

    public static interface ICommentReaction extends IBaseReaction {
        Long getCommentId();

        Boolean getIsReply();
    }
}
