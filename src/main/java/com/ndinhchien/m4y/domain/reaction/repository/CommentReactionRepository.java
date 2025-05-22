package com.ndinhchien.m4y.domain.reaction.repository;

import com.ndinhchien.m4y.domain.reaction.entity.CommentReaction;
import com.ndinhchien.m4y.domain.user.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    Optional<CommentReaction> findByIsReplyAndCommentIdAndUser(Boolean isReply, Long commentId, User user);

}
