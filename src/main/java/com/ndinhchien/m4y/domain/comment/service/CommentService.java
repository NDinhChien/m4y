package com.ndinhchien.m4y.domain.comment.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.comment.dto.CommentRequestDto.AddCommentDto;
import com.ndinhchien.m4y.domain.comment.dto.CommentRequestDto.AddReplyDto;
import com.ndinhchien.m4y.domain.comment.dto.CommentRequestDto.UpdateCommentDto;
import com.ndinhchien.m4y.domain.comment.dto.CommentResponseDto.IComment;
import com.ndinhchien.m4y.domain.comment.entity.Comment;
import com.ndinhchien.m4y.domain.comment.entity.Reply;
import com.ndinhchien.m4y.domain.comment.repository.CommentRepository;
import com.ndinhchien.m4y.domain.comment.repository.ReplyRepository;
import com.ndinhchien.m4y.domain.comment.type.BaseComment;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.service.ProjectService;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;

    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public List<IComment> getComments(Long projectId) {
        Project project = projectService.validateProject(projectId);
        return commentRepository.findAllByProject(project);
    }

    @Transactional
    public Comment addComment(User user, AddCommentDto requestDto) {
        Long projectId = requestDto.getProjectId();
        String content = requestDto.getContent();

        Project project = projectService.validateProject(projectId);
        return commentRepository.save(new Comment(user, project, content));
    }

    @Transactional
    public Reply addReply(User user, AddReplyDto requestDto) {
        Long commentId = requestDto.getCommentId();
        String content = requestDto.getContent();

        Comment comment = validateComment(commentId);

        Reply reply = new Reply(user, comment, content);
        return replyRepository.save(reply);
    }

    @Transactional
    public BaseComment updateComment(User user, UpdateCommentDto requestDto) {

        Boolean isReply = requestDto.getIsReply();
        Long commentId = requestDto.getCommentId();

        String content = requestDto.getContent();
        Boolean isDeleted = requestDto.getIsDeleted();

        if (isReply) {
            Reply reply = validateReply(commentId);
            if (!reply.isAuthor(user)) {
                throw new BusinessException(HttpStatus.FORBIDDEN, "You are not author of this reply");
            }
            reply.update(content, isDeleted);
            return replyRepository.save(reply);
        }

        Comment comment = validateComment(commentId);
        if (!comment.isAuthor(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "You are not author of this comment");
        }
        comment.update(content, isDeleted);
        return commentRepository.save(comment);
    }

    private Comment validateComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.COMMENT_NOT_FOUND);
        });
    }

    private Reply validateReply(Long replyId) {
        return replyRepository.findById(replyId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.REPLY_NOT_FOUND);
        });
    }
}
