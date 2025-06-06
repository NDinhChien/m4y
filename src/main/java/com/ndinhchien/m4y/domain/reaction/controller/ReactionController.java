package com.ndinhchien.m4y.domain.reaction.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToCommentDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToMessageDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToProjectDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToProposalDto;
import com.ndinhchien.m4y.domain.reaction.entity.MessageReaction;
import com.ndinhchien.m4y.domain.reaction.service.ReactionService;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "react", description = "React Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/react")
@RestController
public class ReactionController {
     private final ReactionService reactionService;

     @Operation(summary = "Get reactions")
     @GetMapping("/all")
     public BaseResponse<?> getReactions(
               @AuthenticationPrincipal UserDetailsImpl userDetails) {
          return BaseResponse.success("User reactions",
                    reactionService.getReactions(userDetails.getUser()));
     }

     @Operation(summary = "React to proposal")
     @PutMapping("/proposal")
     public BaseResponse<?> reactToProposal(
               @AuthenticationPrincipal UserDetailsImpl userDetails,
               @RequestBody @Valid ReactToProposalDto requestDto) {

          return BaseResponse.success("React to proposal",
                    reactionService.reactToProposal(userDetails.getUser(), requestDto));
     }

     @Operation(summary = "React to project")
     @PutMapping("/project")
     public BaseResponse<?> reactToProject(
               @AuthenticationPrincipal UserDetailsImpl userDetails,
               @RequestBody @Valid ReactToProjectDto requestDto) {
          return BaseResponse.success("React to project",
                    reactionService.reactToProject(userDetails.getUser(), requestDto));
     }

     @Operation(summary = "React to comment")
     @PutMapping("/comment")
     public BaseResponse<?> reactToComment(
               @AuthenticationPrincipal UserDetailsImpl userDetails,
               @RequestBody @Valid ReactToCommentDto requestDto) {
          return BaseResponse.success("React to comment",
                    reactionService.reactToComment(userDetails.getUser(), requestDto));
     }

     @Operation(summary = "React to message")
     @PutMapping("/message")
     public BaseResponse<MessageReaction> reactToMessage(
               @AuthenticationPrincipal UserDetailsImpl userDetails,
               @RequestBody @Valid ReactToMessageDto requestDto) {
          return BaseResponse.success("React to message",
                    reactionService.reactToMessage(userDetails.getUser(), requestDto));
     }
}
