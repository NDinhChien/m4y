package com.ndinhchien.m4y.domain.project.entity;

import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.proposal.entity.Language;
import com.ndinhchien.m4y.domain.subtitle.entity.Subtitle;
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
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "projectCache")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "videos", indexes = {
        @Index(name = "videos_channel_id_idx", columnList = "channel_id"),
})
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_id", nullable = false, insertable = false, updatable = false)
    private Long channelId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(nullable = false, unique = true)
    private String url;

    @Column
    private String name;

    @Column(length = 1024)
    private String description;

    @Column
    private String image;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private String langCode;

    @Column(name = "creator_id", insertable = false, updatable = false)
    private Long creatorId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @JsonIgnore
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Subtitle> subtitles;

    @JsonIgnore
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Project> projects;

    @Builder
    public Video(Channel channel, String url, String name, String description, String image, Integer duration,
            Language language) {
        this.channel = channel;
        this.channelId = channel.getId();
        this.url = url;
        this.name = name;
        this.description = description;
        this.image = image;
        this.duration = duration;
        this.langCode = language.getCode();
    }

    public boolean hasCreator() {
        return this.creatorId != null;
    }

    public void setCreator(User creator) {
        if (this.creatorId == null) {
            this.creator = creator;
            this.creatorId = creator.getId();
        }
    }

    public boolean isCreator(User user) {
        if (this.creatorId == null)
            return true;
        return user.getId().equals(this.creatorId);
    }

}
