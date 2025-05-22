package com.ndinhchien.m4y.domain.message.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.message.dto.MessageRequestDto.UpdateMessageDto;
import com.ndinhchien.m4y.domain.reaction.entity.MessageReaction;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "messageCache")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "messages_created_at_idx", columnList = "created_at"),
})
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 1024)
    private String content;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant editedAt;

    @Column(nullable = false)
    private Integer reactCount;

    @JsonIgnore
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MessageReaction> reactions;

    @PrePersist
    private void prePersist() {
        if (isDeleted == null) {
            isDeleted = true;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (editedAt == null) {
            editedAt = Instant.now();
        }
        if (reactCount == null) {
            reactCount = 0;
        }
    }

    public Message(User user, String content) {
        this.user = user;
        this.userId = user.getId();
        this.content = content;

        this.prePersist();
    }

    public boolean isAuthor(User user) {
        return this.userId.equals(user.getId());
    }

    public void updateReactCount(int count) {
        this.reactCount = Math.max(0, this.reactCount + count);
    }

    public boolean canEdit() {
        return this.createdAt.plus(1, ChronoUnit.MINUTES).isAfter(Instant.now());
    }

    public void update(UpdateMessageDto dto) {
        String content = dto.getContent();
        Boolean isDeleted = dto.getIsDeleted();
        if (!canEdit()) {
            return;
        }
        if (StringUtils.hasText(content)) {
            this.content = content;
            this.editedAt = Instant.now();
        }
        if (isDeleted != null) {
            this.isDeleted = isDeleted;
        }
    }
}
