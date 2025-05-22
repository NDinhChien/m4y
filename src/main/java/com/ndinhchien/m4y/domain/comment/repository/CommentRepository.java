package com.ndinhchien.m4y.domain.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndinhchien.m4y.domain.comment.dto.CommentResponseDto.IComment;
import com.ndinhchien.m4y.domain.comment.entity.Comment;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByProjectAndUser(Project project, User user);

    List<IComment> findAllByProject(Project project);
}
