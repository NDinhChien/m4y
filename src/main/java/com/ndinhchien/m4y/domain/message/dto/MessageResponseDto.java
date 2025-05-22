package com.ndinhchien.m4y.domain.message.dto;

import java.time.Instant;
import java.util.List;

import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IMessageReaction;

public class MessageResponseDto {
    public static interface IMessage {
        Long getId();

        Long getUserId();

        String getContent();

        String getIsDeleted();

        Instant getCreatedAt();

        Instant getEditedAt();

        Integer getReactCount();

        List<IMessageReaction> getReactions();
    }
}
