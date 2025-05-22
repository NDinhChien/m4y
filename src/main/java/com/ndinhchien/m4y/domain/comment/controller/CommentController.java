package com.ndinhchien.m4y.domain.comment.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.comment.dto.CommentRequestDto.AddCommentDto;
import com.ndinhchien.m4y.domain.comment.dto.CommentRequestDto.AddReplyDto;
import com.ndinhchien.m4y.domain.comment.dto.CommentRequestDto.UpdateCommentDto;
import com.ndinhchien.m4y.domain.comment.dto.CommentResponseDto.IComment;
import com.ndinhchien.m4y.domain.comment.entity.Comment;
import com.ndinhchien.m4y.domain.comment.entity.Reply;
import com.ndinhchien.m4y.domain.comment.service.CommentService;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "comment", description = "Comment Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@RestController
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Get project's comments")
    @GetMapping("/all")
    public BaseResponse<List<IComment>> getComments(
            @RequestParam Long projectId) {
        return BaseResponse.success("Project's comments", commentService.getComments(projectId));

    }

    @Operation(summary = "Leave a comment")
    @PostMapping
    public BaseResponse<Comment> addComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid AddCommentDto requestDto) {
        return BaseResponse.success("Comment added", commentService.addComment(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Reply to comment")
    @PostMapping("/reply")
    public BaseResponse<Reply> responseToComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid AddReplyDto requestDto) {
        return BaseResponse.success("Reply added",
                commentService.addReply(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Update comment")
    @PutMapping
    public BaseResponse<?> updateComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateCommentDto requestDto) {
        return BaseResponse.success("Comment updated",
                commentService.updateComment(userDetails.getUser(), requestDto));
    }

}
