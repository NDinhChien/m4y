package com.ndinhchien.m4y.domain.comment.type;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@MappedSuperclass
public class BaseComment implements Serializable {

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 1024)
    private String content;

    @Column(nullable = false)
    private Integer reactCount;

    @Column(nullable = false)
    private Instant editedAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Boolean isDeleted;

    @PrePersist
    private void prePersist() {
        if (isDeleted == null) {
            isDeleted = false;
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

    public BaseComment(User user, String content) {
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

    public void update(String content, Boolean isDeleted) {
        if (StringUtils.hasText(content)) {
            this.content = content;
            this.editedAt = Instant.now();
        }
        if (isDeleted != null) {
            this.isDeleted = isDeleted;
        }
    }

}
