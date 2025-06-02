package com.ndinhchien.m4y.domain.user.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "followers", indexes = {
        @Index(name = "followers_user_id_idx", columnList = "user_id"),
        @Index(name = "followers_target_id_idx", columnList = "target_id"),
})
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "target_id", nullable = false, insertable = false, updatable = false)
    private Long targetId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private User target;

    @Column(nullable = false)
    private Instant createdAt;

    public Follower(User user, User target) {
        this.user = user;
        this.userId = user.getId();
        this.target = target;
        this.targetId = target.getId();

        this.createdAt = Instant.now();
    }
}
