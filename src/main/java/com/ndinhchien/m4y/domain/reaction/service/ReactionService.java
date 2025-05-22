package com.ndinhchien.m4y.domain.reaction.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.address.entity.Country;
import com.ndinhchien.m4y.domain.address.entity.Deanery;
import com.ndinhchien.m4y.domain.address.entity.Diocese;
import com.ndinhchien.m4y.domain.address.entity.Language;
import com.ndinhchien.m4y.domain.address.entity.Parish;
import com.ndinhchien.m4y.domain.address.repository.LanguageRepository;
import com.ndinhchien.m4y.domain.address.service.AddressService;
import com.ndinhchien.m4y.domain.address.service.LanguageService;
import com.ndinhchien.m4y.domain.address.type.AddresssType;
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
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToCommentDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToMessageDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToProjectDto;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionRequestDto.ReactToProposalDto;
import com.ndinhchien.m4y.domain.reaction.entity.ProposalReaction;
import com.ndinhchien.m4y.domain.reaction.entity.CommentReaction;
import com.ndinhchien.m4y.domain.reaction.entity.MessageReaction;
import com.ndinhchien.m4y.domain.reaction.entity.ProjectReaction;
import com.ndinhchien.m4y.domain.reaction.repository.ProposalReactionRepository;
import com.ndinhchien.m4y.domain.reaction.repository.CommentReactionRepository;
import com.ndinhchien.m4y.domain.reaction.repository.MessageReactionRepository;
import com.ndinhchien.m4y.domain.reaction.repository.ProjectReactionRepository;
import com.ndinhchien.m4y.domain.reaction.type.Emoji;
import com.ndinhchien.m4y.domain.reaction.type.BaseReaction;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.entity.Proposable;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;
import com.ndinhchien.m4y.global.service.MessageManager;
import com.ndinhchien.m4y.global.websocket.MessageDestination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReactionService {

    private final LanguageService languageService;

    private final AddressService addressService;

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

    @Transactional
    public ProposalReaction reactToProposal(User user, ReactToProposalDto requestDto) {
        String name = requestDto.getProposalName();
        AddresssType type = requestDto.getProposalType();
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

    private Proposable updateProposal(AddresssType type, String name, int count) {
        if (type == AddresssType.LANGUAGE) {
            Language language = languageService.validateLanguage(name);
            language.updateReactCount(count);
            return languageService.save(language);
        }
        if (type == AddresssType.COUNTRY) {
            Country country = addressService.validateCountry(name);
            country.updateReactCount(count);
            return addressService.save(country);
        }
        if (type == AddresssType.DIOCESE) {
            Diocese diocese = addressService.validateDiocese(name);
            diocese.updateReactCount(count);
            return addressService.save(diocese);
        }
        if (type == AddresssType.DEANERY) {
            Deanery deanery = addressService.validateDeanary(name);
            deanery.updateReactCount(count);
            return addressService.save(deanery);
        }
        if (type == AddresssType.PARISH) {
            Parish parish = addressService.validateParish(name);
            parish.updateReactCount(count);
            return addressService.save(parish);
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid target type");
    }
}
