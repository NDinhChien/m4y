package com.ndinhchien.m4y.domain.project.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.comment.entity.Comment;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.UpdateProjectDto;
import com.ndinhchien.m4y.domain.proposal.entity.Language;
import com.ndinhchien.m4y.domain.reaction.entity.ProjectReaction;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "projectCache")
@DynamicUpdate
@NoArgsConstructor
@Getter
@Entity
@Table(name = "projects", indexes = {
        @Index(name = "projects_admin_id_idx", columnList = "admin_id"),
        @Index(name = "projects_video_url_idx", columnList = "video_url"),
        @Index(name = "projects_channel_url_idx", columnList = "channel_url"),
        @Index(name = "projects_lang_code_idx", columnList = "lang_code"),
        @Index(name = "projects_created_at_idx", columnList = "created_at"),
        @Index(name = "projects_view_count_idx", columnList = "view_count"),
})
public class Project implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "channel_id", nullable = false, insertable = false, updatable = false)
    private Long channelId;

    @Column(nullable = false)
    private String channelUrl;

    @Column
    private String channelImage;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "video_id", nullable = false, insertable = false, updatable = false)
    private Long videoId;

    @Column(nullable = false)
    private String videoUrl;

    @Column
    private String videoImage;

    @Column
    private String name;

    @Column(length = 1024)
    private String description;

    @JsonIgnore
    @Column(columnDefinition = "tsvector", insertable = false, updatable = false)
    private String fullText;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private String langCode;

    @Column(nullable = false)
    private Integer viewCount;

    @Column(nullable = false)
    private Integer reactCount;

    @Column(name = "admin_id", nullable = false, insertable = false, updatable = false)
    private Long adminId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Request> requests;

    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectReaction> reactions;

    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ViewHistory> views;

    @PrePersist
    private void prePersist() {
        if (viewCount == null) {
            viewCount = 0;
        }
        if (reactCount == null) {
            reactCount = 0;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
        if (isCompleted == null) {
            isCompleted = false;
        }
    }

    @Builder
    public Project(User admin, Channel channel, Video video, String name, String description, Language language) {
        this.admin = admin;
        this.adminId = admin.getId();
        this.channel = channel;
        this.channelId = channel.getId();
        this.channelUrl = channel.getUrl();
        this.channelImage = channel.getImage();

        this.video = video;
        this.videoId = video.getId();
        this.videoUrl = video.getUrl();
        this.videoImage = video.getImage();
        this.duration = video.getDuration();

        this.name = name;
        this.description = description;
        this.langCode = language.getCode();

        this.prePersist();
    }

    public boolean isAdmin(User user) {
        return this.adminId.equals(user.getId());
    }

    public void setLangCode(Language language) {
        this.langCode = language.getCode();
    }

    public void updateReactCount(int count) {
        this.reactCount = Math.max(0, this.reactCount + count);
    }

    public void updateViewCount(int count) {
        this.viewCount = Math.max(1, this.viewCount + count);
    }

    public void update(UpdateProjectDto dto) {
        String name = dto.getName();
        String description = dto.getDescription();
        Boolean isCompleted = dto.getIsCompleted();
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
        if (StringUtils.hasText(description)) {
            this.description = description;
        }
        if (isCompleted == true) {
            this.isCompleted = true;
        }
    }

}
