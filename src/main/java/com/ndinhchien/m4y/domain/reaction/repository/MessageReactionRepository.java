package com.ndinhchien.m4y.domain.reaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.message.entity.Message;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IMessageReaction;
import com.ndinhchien.m4y.domain.reaction.entity.MessageReaction;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {

    Optional<MessageReaction> findByMessageAndUser(Message message, User user);

    List<IMessageReaction> findAllByUser(User user);
}
