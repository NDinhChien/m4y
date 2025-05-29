package com.ndinhchien.m4y.domain.project.dto;

import java.time.Instant;
import java.util.List;

import com.ndinhchien.m4y.domain.comment.dto.CommentResponseDto.IComment;
import com.ndinhchien.m4y.domain.reaction.dto.ReactionResponseDto.IProjectReaction;

public class ProjectResponseDto {
    public static interface IBasicChannel {

        Long getId();

        String getName();

        String getUrl();

        String getDescription();

        String getImage();

    }

    public static interface IChannel extends IBasicChannel {
        List<IBasicVideo> getVideos();
    }

    public static interface IBasicVideo {
        Long getId();

        Long getChannelId();

        String getUrl();

        String getName();

        String getDescription();

        String getImage();

        Integer getDuration();

        String getLangCode();

        String getCreatorId();

    }

    public static interface IVideo extends IBasicVideo {
        IBasicChannel getChannel();

        List<IBasicProject> getProjects();
    }

    public static interface IRequest {
        Long getId();

        Long getUserId();

        Long getProjectId();

        Integer getStatus();

        Instant getCreatedAt();

        Instant getUpdatedAt();

    }

    public interface IBasicProject {
        Long getId();

        Long getChannelId();

        String getChannelUrl();

        String getChannelImage();

        Long getVideoId();

        String getVideoUrl();

        String getVideoImage();

        String getName();

        String getDescription();

        Integer getDuration();

        String getLangCode();

        Integer getViewCount();

        Integer getReactCount();

        Long getAdminId();

        Boolean getIsCompleted();

        Instant getCreatedAt();

        Instant getUpdatedAt();
    }

    public interface IBasicProjectWithRequest extends IBasicProject {

        List<IRequest> getRequests();

    }

    public interface IProject extends IBasicProject {

        List<IProjectReaction> getReactions();

        List<IComment> getComments();
    }

    public interface IProjectSearch extends IBasicProject {

        Float getRelevance();
    }
}
