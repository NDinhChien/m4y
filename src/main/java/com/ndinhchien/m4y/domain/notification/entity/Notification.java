package com.ndinhchien.m4y.domain.notification.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndinhchien.m4y.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "notifications_user_id_created_at_idx", columnList = "user_id, created_at"),
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Boolean isViewed;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    private void prePersist() {
        if (isViewed == null) {
            isViewed = false;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Notification(User user, String content) {
        this.user = user;
        this.userId = user.getId();
        this.content = content;

        this.prePersist();
    }

    public boolean isRecipient(User user) {
        return this.userId.equals(user.getId());
    }

    public void updateIsViewed() {
        this.isViewed = true;
    }
}
