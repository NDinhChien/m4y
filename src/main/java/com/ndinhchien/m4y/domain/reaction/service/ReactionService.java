package com.ndinhchien.m4y.domain.reaction.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.comment.entity.Comment;
import com.ndinhchien.m4y.domain.comment.entity.Reply;
import com.ndinhchien.m4y.domain.comment.repository.CommentRepository;
import com.ndinhchien.m4y.domain.comment.repository.ReplyRepository;
import com.ndinhchien.m4y.domain.comment.type.BaseComment;
import com.ndinhchien.m4y.domain.message.entity.Message;
import com.ndinhchien.m4y.domain.message.repository.MessageRepository;
import com.ndinhchien.m4y.domain.message.service.MessageService;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.repository.ProjectRepository;
import com.ndinhchien.m4y.domain.proposal.entity.Country;
import com.ndinhchien.m4y.domain.proposal.entity.Deanery;
import com.ndinhchien.m4y.domain.proposal.entity.Diocese;
import com.ndinhchien.m4y.domain.proposal.entity.Language;
import com.ndinhchien.m4y.domain.proposal.entity.Parish;
import com.ndinhchien.m4y.domain.proposal.entity.Proposable;
import com.ndinhchien.m4y.domain.proposal.repository.LanguageRepository;
import com.ndinhchien.m4y.domain.proposal.service.ProposalService;
import com.ndinhchien.m4y.domain.proposal.type.ProposalType;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToCommentDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToMessageDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToProjectDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToProposalDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IProposalReaction;
import com.ndinhchien.m4y.domain.reaction.entity.ProposalReaction;
import com.ndinhchien.m4y.domain.reaction.entity.CommentReaction;
import com.ndinhchien.m4y.domain.reaction.entity.MessageReaction;
import com.ndinhchien.m4y.domain.reaction.entity.ProjectReaction;
import com.ndinhchien.m4y.domain.reaction.repository.ProposalReactionRepository;
import com.ndinhchien.m4y.domain.reaction.repository.CommentReactionRepository;
import com.ndinhchien.m4y.domain.reaction.repository.MessageReactionRepository;
import com.ndinhchien.m4y.domain.reaction.repository.ProjectReactionRepository;
import com.ndinhchien.m4y.domain.reaction.type.Emoji;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;
import com.ndinhchien.m4y.global.websocket.MessageDestination;
import com.ndinhchien.m4y.global.websocket.MessageManager;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReactionService {

    private final LanguageRepository languageRepository;

    private final ProposalService proposalService;

    private final MessageService messageService;

    private final ProposalReactionRepository proposalReactionRepository;

    private final ProjectReactionRepository projectReactionRepository;

    private final MessageReactionRepository messageReactionRepository;

    private final CommentReactionRepository commentReactionRepository;

    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;

    private final ProjectRepository projectRepository;

    private final MessageRepository messageRepository;

    private final MessageManager messageManager;

    @Transactional(readOnly = true)
    public List<IProposalReaction> getProposalReactions(User user) {
        return proposalReactionRepository.findAllByUser(user);
    }

    @Transactional
    public ProposalReaction reactToProposal(User user, ReactToProposalDto requestDto) {
        String name = requestDto.getProposalName();
        ProposalType type = requestDto.getProposalType();
        Emoji emoji = requestDto.getEmoji();
        Boolean isDeleted = requestDto.getIsDeleted();

        ProposalReaction reaction = proposalReactionRepository
                .findByProposalTypeAndProposalNameAndUser(type, name, user)
                .orElse(null);

        int count = 0;
        if (reaction == null) {
            reaction = new ProposalReaction(user, emoji, type, name);
            count = 1;
        } else {
            count = reaction.update(emoji, isDeleted);
        }

        updateProposal(type, name, count);
        return proposalReactionRepository.save(reaction);

    }

    @Transactional
    public ProjectReaction reactToProject(User user, ReactToProjectDto requestDto) {
        Long projecId = requestDto.getProjectId();
        Project project = validateProject(projecId);
        Emoji emoji = requestDto.getEmoji();
        Boolean isDeleted = requestDto.getIsDeleted();

        ProjectReaction reaction = projectReactionRepository.findByProjectAndAndUser(project, user).orElse(null);
        int count = 0;
        if (reaction == null) {
            reaction = new ProjectReaction(user, emoji, project);
            count = 1;
        } else {
            count = reaction.update(emoji, isDeleted);
        }
        project.updateReactCount(count);
        projectRepository.save(project);

        return projectReactionRepository.save(reaction);
    }

    @Transactional
    public CommentReaction reactToComment(User user, ReactToCommentDto requestDto) {
        Long commentId = requestDto.getCommentId();
        Boolean isReply = requestDto.getIsReply();
        Emoji emoji = requestDto.getEmoji();
        Boolean isDeleted = requestDto.getIsDeleted();

        BaseComment comment = validateComment(isReply, commentId);

        CommentReaction reaction = commentReactionRepository
                .findByIsReplyAndCommentIdAndUser(isReply, commentId, user).orElse(null);

        int count = 0;
        if (reaction == null) {
            reaction = new CommentReaction(user, emoji, commentId, isReply);
            count = 1;
        } else {
            count = reaction.update(emoji, isDeleted);
        }

        comment.updateReactCount(count);
        if (isReply) {
            replyRepository.save((Reply) comment);
        } else {
            commentRepository.save((Comment) comment);
        }
        return commentReactionRepository.save(reaction);
    }

    @Transactional
    public MessageReaction reactToMessage(User user, ReactToMessageDto requestDto) {
        Long messageId = requestDto.getMessageId();
        Emoji emoji = requestDto.getEmoji();
        Boolean isDeleted = requestDto.getIsDeleted();
        Message message = messageService.validateMessage(messageId);

        MessageReaction reaction = messageReactionRepository.findByMessageAndUser(message, user).orElse(null);

        int count = 0;
        if (reaction == null) {
            reaction = new MessageReaction(user, emoji, message);
            count = 1;
        } else {
            count = reaction.update(emoji, isDeleted);
        }

        message.updateReactCount(count);
        messageRepository.save(message);
        reaction = messageReactionRepository.save(reaction);
        messageManager.sendToGlobal(MessageDestination.GLOBAL_MESSAGE, reaction);
        return reaction;
    }

    public Project validateProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PROJECT_NOT_FOUND);
        });
    }

    public BaseComment validateComment(Boolean isReply, Long commentId) {
        if (!isReply) {
            return commentRepository.findById(commentId).orElseThrow(() -> {
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.COMMENT_NOT_FOUND);
            });
        }
        return replyRepository.findById(commentId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.REPLY_NOT_FOUND);
        });
    }

    public Language validateLanguage(@NotNull String langName) {

        return languageRepository.findByName(langName).orElseThrow(() -> {

            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.LANGUAGE_NOT_FOUND);
        });
    }

    private Proposable updateProposal(ProposalType type, String name, int count) {
        if (type == ProposalType.LANGUAGE) {
            Language language = validateLanguage(name);
            language.updateReactCount(count);
            return languageRepository.save(language);
        }
        if (type == ProposalType.COUNTRY) {
            Country country = proposalService.validateCountry(name);
            country.updateReactCount(count);
            return proposalService.save(country);
        }
        if (type == ProposalType.DIOCESE) {
            Diocese diocese = proposalService.validateDiocese(name);
            diocese.updateReactCount(count);
            return proposalService.save(diocese);
        }
        if (type == ProposalType.DEANERY) {
            Deanery deanery = proposalService.validateDeanary(name);
            deanery.updateReactCount(count);
            return proposalService.save(deanery);
        }
        if (type == ProposalType.PARISH) {
            Parish parish = proposalService.validateParish(name);
            parish.updateReactCount(count);
            return proposalService.save(parish);
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid target type");
    }
}
