package com.ndinhchien.m4y.domain.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.comment.entity.Comment;
import com.ndinhchien.m4y.domain.comment.entity.Reply;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByComment(Comment comment);

    Optional<Reply> findByCommentAndUser(Comment comment, User user);
}
