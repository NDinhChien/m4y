package com.ndinhchien.m4y.domain.project.dto;

import java.time.Instant;
import java.util.List;

import com.ndinhchien.m4y.domain.comment.dto.CommentResponseDto.IComment;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IProjectReaction;

public class ProjectResponseDto {
    public static interface IChannel {
        Long getId();

        String getName();

        String getUrl();
    }

    public static interface IProjectTranslator {
        Long getId();

        Long getUserId();

        Long getProjectId();

        Boolean getIsAccepted();

        Instant getCreatedAt();

    }

    public interface IBasicProject {
        Long getId();

        String getSrcUrl();

        Long getChannelId();

        String getTitle();

        String getDescription();

        Integer getDuration();

        String getSrcLangCode();

        String getDesLangCode();

        Integer getViewCount();

        Integer getReactCount();

        Long getAdminId();

        Instant getCreatedAt();

        Instant getUpdatedAt();
    }

    public interface IProject extends IBasicProject {

        List<IProjectTranslator> getTranslators();

        List<IProjectReaction> getReactions();

        List<IComment> getComments();
    }

    public interface IProjectSearch extends IBasicProject {

        Float getRelevance();
    }
}
